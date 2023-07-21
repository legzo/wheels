package io.jterrier.wheels.database

import org.jetbrains.exposed.dao.id.IntIdTable

object ActivitiesTable: IntIdTable() {

    val stravaId = long("strava_id")
    val name = varchar("name", 200)
    val distance = float("distance")
    val polyline = varchar("polyline", 4_000)

}