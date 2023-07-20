import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.Lens

data class ClientId(val value: String)
data class ClientSecret(val value: String)
data class RefreshToken(val value: String)

data class SpotifyCredentials(
    val clientId: ClientId,
    val clientSecret: ClientSecret,
    val refreshToken: RefreshToken,
)

val env = Environment.ENV


val clientIdLens: Lens<Environment, ClientId> = EnvironmentKey.map(::ClientId).required("STRAVA_CLIENT_ID")
val clientSecretLens: Lens<Environment, ClientSecret> = EnvironmentKey.map(::ClientSecret).required("STRAVA_CLIENT_SECRET")
val refreshTokenLens: Lens<Environment, RefreshToken> = EnvironmentKey.map(::RefreshToken).required("STRAVA_REFRESH_TOKEN")


val stravaApiConfig = SpotifyCredentials(
    clientId = clientIdLens(env),
    clientSecret = clientSecretLens(env),
    refreshToken = refreshTokenLens(env),
)