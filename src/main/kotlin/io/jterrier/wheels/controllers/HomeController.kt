package io.jterrier.wheels.controllers

import io.jterrier.wheels.services.ActivitiesService
import io.jterrier.wheels.services.StatsService
import io.jterrier.wheels.views.HomeView
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Query
import org.http4k.lens.int

class HomeController(
    private val activitiesService: ActivitiesService,
    private val statsService: StatsService,
) {

    private val distanceQuery = Query.int().defaulted("min_distance", 20)

    fun display(request: Request): Response {

        val minDistanceInKm = distanceQuery(request)
        val minDistanceInMeters = minDistanceInKm * 1_000

        val allActivities = activitiesService.refreshAndGetAllActivities()

        val activitiesToDisplay = allActivities
            .sortedByDescending { it.startTime }
            .filter { it.distanceInMeters > minDistanceInMeters }

        return Response(Status.OK).body(
            HomeView(
                heatMapActivities = activitiesToDisplay,
                totalNbOfActivities = allActivities.size,
                monthlyDistances = statsService.getMonthlyDistances(allActivities),
                scatterPlotData = statsService.getScatterPlotData(allActivities
                    .filter { !it.isCommute }
                    .filter { it.isInLastXYears(4) }
                ),
                yearlyStats = statsService.getYearlyStats(allActivities)
            )
                .display()
        )
    }

}