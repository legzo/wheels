package io.jterrier.wheels.controllers

import io.jterrier.wheels.Activity
import io.jterrier.wheels.StravaConnector
import io.jterrier.wheels.toKm
import io.jterrier.wheels.views.MapsListView
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Query
import org.http4k.lens.int

class MapsListController(private val stravaConnector: StravaConnector) {

    val distanceQuery = Query.int().defaulted("min_distance", 20)

    fun displayMaps(request: Request): Response {

        val minDistanceInKm = distanceQuery(request)
        val minDistanceInMeters = minDistanceInKm * 1_000

        val activities = stravaConnector
            .getActivities()
            .filter { it.distance > minDistanceInMeters }
            .map {
                Activity(
                    id = it.id,
                    name = it.name,
                    distance = it.distance.toKm(),
                    polyline = it.map.summaryPolyline
                )
            }

        return Response(Status.OK).body(MapsListView(activities, minDistanceInKm).display())
    }

}