package io.jterrier.wheels

import kotlinx.datetime.Instant

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
)

data class Route(
    val id: String,
    val name: String,
)