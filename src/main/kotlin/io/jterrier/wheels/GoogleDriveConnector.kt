package io.jterrier.wheels

import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.valueOrNull
import io.jterrier.wheels.dtos.FileDto
import io.jterrier.wheels.dtos.FileListDto
import io.jterrier.wheels.dtos.FileModifiedTimeDto
import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters
import org.http4k.format.Jackson.auto
import org.http4k.lens.WebForm
import org.http4k.security.ContentTypeJsonOrForm
import org.http4k.security.CredentialsProvider
import org.http4k.security.ExpiringCredentials
import org.http4k.security.OAuthWebForms
import org.http4k.security.RefreshCredentials
import org.http4k.security.Refreshing
import org.http4k.security.oauth.core.RefreshToken
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant
import kotlin.time.measureTimedValue

class GoogleDriveConnector {

    private val callLogMessage = "[{}ms] for {}"

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val apiAuthUrl = "https://www.googleapis.com/oauth2/v4/token"
    private val apiUrl = "https://www.googleapis.com/drive/v3/files"

    private val client: HttpHandler = OkHttp()

    private val fileListLens = Body.auto<FileListDto>().toLens()
    private val fileModifiedTimeLens = Body.auto<FileModifiedTimeDto>().toLens()

    private val refreshTokenFn = RefreshCredentials<String> { oldToken ->

        val tokenToSend = oldToken?.let { RefreshToken(it) }
            ?: RefreshToken(googleApiConfig.refreshToken.value)

        val clientAuth = ClientFilters.BasicAuth(googleApiConfig.clientId.value, googleApiConfig.clientSecret.value)
            //.then(DebuggingFilters.PrintRequestAndResponse())
            .then(client)

        val tokenResponse = clientAuth(
            Request(Method.POST, apiAuthUrl)
                .with(
                    OAuthWebForms.requestForm of WebForm()
                        .with(
                            OAuthWebForms.grantType of "refresh_token",
                            OAuthWebForms.redirectUri of Uri.of("https://localhost8888/callback"),
                            OAuthWebForms.clientId of googleApiConfig.clientId.value,
                            OAuthWebForms.clientSecret of googleApiConfig.clientSecret.value,
                            OAuthWebForms.refreshToken of tokenToSend,
                            OAuthWebForms.scope of "https://www.googleapis.com/auth/drive"
                        )
                )
        )

        if (tokenResponse.status != Status.OK) {
            logger.error("Error getting token, HTTP ${tokenResponse.status}")
            logger.error(tokenResponse.bodyString())
        }

        tokenResponse.takeIf { it.status.successful }
            ?.let { ContentTypeJsonOrForm()(it).map { tokenDetails -> tokenDetails.accessToken }.valueOrNull() }
            ?.let { accessToken ->
                ExpiringCredentials(
                    accessToken.value,
                    accessToken.expiresIn?.let { Clock.systemUTC().instant().plusSeconds(it) } ?: Instant.MAX
                )
            }
    }

    private val refreshingTokenClient = ClientFilters
        .BearerAuth(CredentialsProvider.Refreshing(refreshFn = refreshTokenFn))
        //.then(DebuggingFilters.PrintRequestAndResponse())
        .then(client)


    fun getFiles(): List<FileDto> {
        val uri =
            "$apiUrl?orderBy=modifiedTime desc&q='1AKhoENTeqeExaXyvd7tNncekqohBm5yB' in parents and name contains '.gpx'"
        val (fileList, duration) = measureTimedValue { fileListLens(refreshingTokenClient(Request(GET, uri))) }
        logger.info(callLogMessage, duration.inWholeMilliseconds,  uri)
        logger.info("Got {} results", fileList.files.size)
        return fileList.files
    }

    fun getFileContents(id: String): String {
        val uri = "$apiUrl/$id?alt=media"
        val (response, duration) = measureTimedValue { refreshingTokenClient(Request(GET, uri)) }
        logger.info(callLogMessage, duration.inWholeMilliseconds,  uri)
        return response.bodyString()
    }

    fun getFileModifiedTime(id: String): String {
        val uri = "$apiUrl/$id?fields=modifiedTime"
        val (response, duration) = measureTimedValue { fileModifiedTimeLens(refreshingTokenClient(Request(GET, uri))) }
        logger.info(callLogMessage, duration.inWholeMilliseconds,  uri)
        return response.modifiedTime
    }

}