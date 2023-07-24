package io.jterrier.wheels.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class ActivityDto(
    val id: Long,
    val name: String,
    val distance: Double,
    @JsonProperty("moving_time")
    val movingTime: Int,
    @JsonProperty("elapsed_time")
    val elapsedTime: Int,
    @JsonProperty("total_elevation_gain")
    val totalElevationGain: Int,
    val type: String,
    @JsonProperty("start_date")
    val startDate: String,
    @JsonProperty("average_speed")
    val averageSpeed: Float,
    @JsonProperty("max_speed")
    val maxSpeed: Float,
    val map: MapDto
)

data class MapDto(

    @JsonProperty("summary_polyline")
    val summaryPolyline: String
)