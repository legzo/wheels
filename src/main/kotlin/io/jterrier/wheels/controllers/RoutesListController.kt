package io.jterrier.wheels.controllers

import io.jterrier.wheels.services.RoutesService
import io.jterrier.wheels.views.RoutesListView
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Query
import org.http4k.lens.string
import java.lang.IllegalArgumentException

class RoutesListController(
    private val routesService: RoutesService
) {

    private val routeIdQuery = Query.string().required("id")

    fun display(request: Request) =
        Response(Status.OK).body(
            RoutesListView(routes = routesService.getRoutes())
                .display()
        )

    fun routeDetails(request: Request): Response {
        val loadedRoute = routesService.getRouteWithGpx(routeIdQuery(request))
            ?: throw IllegalArgumentException("Could not find route")

        return Response(Status.OK).body(
            RoutesListView.details(route = loadedRoute.route)
        )
    }

    fun gpx(request: Request): Response {
        val loadedRoute = routesService.getRouteWithGpx(routeIdQuery(request))
            ?: throw IllegalArgumentException("Could not find route")

        return Response(Status.OK)
            .header("Content-Type", "text/html")
            .body(loadedRoute.gpx)
    }
}