package io.jterrier.wheels.controllers

import io.jterrier.wheels.Activity
import io.jterrier.wheels.StravaConnector
import io.jterrier.wheels.database.DatabaseConnector
import io.jterrier.wheels.views.MapsListView
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Query
import org.http4k.lens.int

class MapsListController(
    private val stravaConnector: StravaConnector,
    private val dbConnector: DatabaseConnector,
) {

    val distanceQuery = Query.int().defaulted("min_distance", 20)

    fun displayMaps(request: Request): Response {

        val minDistanceInKm = distanceQuery(request)
        val minDistanceInMeters = minDistanceInKm * 1_000

        val alreadySavedIds = dbConnector.getAll()
            .map { it.id }
            .toSet()

        val allNewActivities = stravaConnector
            .getNewActivities(alreadySavedIds)
            .map {
                Activity(
                    id = it.id,
                    name = it.name,
                    distance = it.distance,
                    polyline = it.map.summaryPolyline
                )
            }

        dbConnector.save(allNewActivities)

        val allActivitiesFromDb = dbConnector.getAll()
        val activitiesToDisplay = allActivitiesFromDb
            .filter { it.distance > minDistanceInMeters }

        return Response(Status.OK).body(
            MapsListView(
                activities = activitiesToDisplay,
                minDistanceInKm = minDistanceInKm,
                totalNbOfActivities = allActivitiesFromDb.size
            )
                .display()
        )
    }

}