package io.jterrier.wheels.controllers

import io.jterrier.wheels.Activity
import io.jterrier.wheels.StatsService
import io.jterrier.wheels.StravaConnector
import io.jterrier.wheels.database.DatabaseConnector
import io.jterrier.wheels.views.HomeView
import kotlinx.datetime.Instant
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Query
import org.http4k.lens.int

class HomeController(
    private val stravaConnector: StravaConnector,
    private val db: DatabaseConnector,
    private val statsService: StatsService,
) {

    private val distanceQuery = Query.int().defaulted("min_distance", 20)

    fun display(request: Request): Response {

        val minDistanceInKm = distanceQuery(request)
        val minDistanceInMeters = minDistanceInKm * 1_000

        val alreadySavedIds = db.getAll()
            .map { it.id }
            .toSet()

        val allNewActivities = stravaConnector
            .getNewActivities(alreadySavedIds)
            .map {
                Activity(
                    id = it.id,
                    name = it.name,
                    startDate = Instant.parse(it.startDate),
                    durationInSeconds = it.elapsedTime,
                    distanceInMeters = it.distance,
                    polyline = it.map.summaryPolyline
                )
            }

        db.save(allNewActivities)

        val allActivitiesFromDb = db.getAll()
        val activitiesToDisplay = allActivitiesFromDb
            .filter { it.distanceInMeters > minDistanceInMeters }

        return Response(Status.OK).body(
            HomeView(
                heatMapActivities = activitiesToDisplay,
                totalNbOfActivities = allActivitiesFromDb.size,
                monthlyDistances = statsService.getMonthlyDistance(allActivitiesFromDb)
            )
                .display()
        )
    }

}