package io.jterrier.wheels.services

import io.jterrier.wheels.Activity
import io.jterrier.wheels.StravaConnector
import io.jterrier.wheels.database.DatabaseConnector
import kotlinx.datetime.Instant

class ActivitiesService(
    private val stravaConnector: StravaConnector,
    private val db: DatabaseConnector,
) {

    fun refreshAndGetAllActivities(): List<Activity> {

        val alreadySavedIds = db.getAllActivities()
            .map { it.id }
            .toSet()

        val allNewActivities =  stravaConnector
            .getNewActivities(alreadySavedIds)
            .map {
                Activity(
                    id = it.id,
                    name = it.name,
                    distanceInMeters = it.distance,
                    durationInSeconds = it.elapsedTime,
                    totalElevationGain = it.totalElevationGain,
                    startTime = Instant.parse(it.startDate),
                    averageSpeed = it.averageSpeed,
                    maxSpeed = it.maxSpeed,
                    polyline = it.map.summaryPolyline,
                    isCommute = it.isCommute,
                )
            }

        db.saveActivities(allNewActivities)

        return db.getAllActivities()

    }
}