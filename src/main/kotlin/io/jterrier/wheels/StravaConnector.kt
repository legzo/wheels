package io.jterrier.wheels

import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.valueOrNull
import io.jterrier.wheels.dtos.ActivityDto
import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Request
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

class StravaConnector {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val apiAuthUrl = "https://www.strava.com/oauth/token"
    private val apiUrl = "https://www.strava.com/api/v3"
    private val pageSize = 50

    private val client: HttpHandler = OkHttp()

    private val activityListLens = Body.auto<List<ActivityDto>>().toLens()

    private val refreshTokenFn = RefreshCredentials<String> { oldToken ->

        val tokenToSend = oldToken?.let { RefreshToken(it) }
            ?: RefreshToken(stravaApiConfig.refreshToken.value)

        val clientAuth = ClientFilters.BasicAuth(stravaApiConfig.clientId.value, stravaApiConfig.clientSecret.value)
            //.then(DebuggingFilters.PrintRequestAndResponse())
            .then(client)

        clientAuth(
            Request(Method.POST, apiAuthUrl)
                .with(
                    OAuthWebForms.requestForm of WebForm()
                        .with(
                            OAuthWebForms.grantType of "refresh_token",
                            OAuthWebForms.redirectUri of Uri.of("https://localhost8888/callback"),
                            OAuthWebForms.clientId of stravaApiConfig.clientId.value,
                            OAuthWebForms.clientSecret of stravaApiConfig.clientSecret.value,
                            OAuthWebForms.refreshToken of tokenToSend,
                            OAuthWebForms.scope of "activity:read_all"
                        )
                )
        ).takeIf { it.status.successful }
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


    private fun getActivitiesByPage(index: Int): List<ActivityDto> {
        val uri = "$apiUrl/activities?per_page=$pageSize&page=$index"
        logger.info(">>> calling strava @ {}", uri)
        val (activities, duration) = measureTimedValue { activityListLens(refreshingTokenClient(Request(GET, uri))) }
        logger.info("    <<< got {} results in {}ms", activities.size, duration.inWholeMilliseconds)
        return activities
    }

    fun getNewActivities(alreadySavedIds: Set<Long>): List<ActivityDto> =
        getNewActivitiesRecur(index = 1, alreadySavedIds)
            .filter { it.type == "Ride" }

    private fun getNewActivitiesRecur(
        index: Int,
        alreadySavedIds: Set<Long>,
    ): List<ActivityDto> {
        val newActivities = getActivitiesByPage(index).filter { it.id !in alreadySavedIds }

        return when (newActivities.size) {
            pageSize -> newActivities + getNewActivitiesRecur(index + 1, alreadySavedIds)
            else -> newActivities
        }
    }
}