package io.jterrier.wheels.controllers

import io.jterrier.wheels.services.RoutesService
import io.jterrier.wheels.views.RoutesListView
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class RoutesListController(
    private val routesService: RoutesService
) {

    fun display(request: Request): Response {
        return Response(Status.OK).body(
            RoutesListView(routes = routesService.getRoutes())
                .display()
        )
    }
}