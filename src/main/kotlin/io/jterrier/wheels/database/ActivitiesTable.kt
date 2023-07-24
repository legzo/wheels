package io.jterrier.wheels.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ActivitiesTable: IntIdTable() {

    val stravaId = long("strava_id")
    val name = varchar("name", 200)
    val distance = double("distance")
    val startTime = timestamp("start_time")
    val durationInSeconds = integer("duration_in_seconds")
    val polyline = varchar("polyline", 4_000)

}