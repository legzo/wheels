package io.jterrier.wheels

import kotlinx.datetime.Instant

data class Activity(
    val id: Long,
    val name: String,
    val startDate: Instant,
    val durationInSeconds: Int,
    val distanceInMeters: Double,
    val polyline: String
)