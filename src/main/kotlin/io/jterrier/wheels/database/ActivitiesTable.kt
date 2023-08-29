package io.jterrier.wheels.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ActivitiesTable: IntIdTable() {

    val stravaId = long("strava_id")
    val name = varchar("name", 200)
    val distance = double("distance")
    val durationInSeconds = integer("duration_in_seconds")
    val totalElevationGain = integer("total_elevation_gain")
    val startTime = timestamp("start_time")
    val averageSpeed = double("average_speed")
    val maxSpeed = double("max_speed")
    val polyline = varchar("polyline", 4_000)
    val isCommute = bool("is_commute")

}