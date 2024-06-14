package io.jterrier.wheels

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import kotlin.time.Duration.Companion.days

data class Activity(
    val id: Long,
    val name: String,
    val distanceInMeters: Double,
    val durationInSeconds: Int,
    val totalElevationGain: Int,
    val startTime: Instant,
    val averageSpeed: Double,
    val maxSpeed: Double,
    val polyline: String,
    val isCommute: Boolean,
) {
    fun isInLastXYears(x: Int): Boolean {
        val now = Clock.System.now()
        return startTime in now.minus((365 * x).days) ..now
    }

    fun isInCurrentYear(): Boolean {
        val timeZone = TimeZone.of(ZoneId.systemDefault().id)
        return startTime.toLocalDateTime(timeZone).year == Clock.System.now().toLocalDateTime(timeZone).year
    }
}

data class Route(
    val id: String,
    val name: String,
    val url: String,
    val lastModified: Instant?
)

data class RouteWithGpx(
    val route: Route,
    val gpx: String
)