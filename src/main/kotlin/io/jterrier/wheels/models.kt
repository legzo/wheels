package io.jterrier.wheels

import kotlinx.datetime.Instant

data class Activity(
    val id: Long,
    val name: String,
    val startDateLocal: Instant,
    val durationInSeconds: Int,
    val distance: Float,
    val polyline: String
)