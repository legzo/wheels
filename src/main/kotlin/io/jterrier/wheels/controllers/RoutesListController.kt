package io.jterrier.wheels.controllers

import io.jterrier.wheels.GoogleDriveConnector
import io.jterrier.wheels.Route
import io.jterrier.wheels.views.RoutesListView
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class RoutesListController(
    private val googleDriveConnector: GoogleDriveConnector
) {

    fun display(request: Request): Response {
        return Response(Status.OK).body(
            RoutesListView(
               routes = googleDriveConnector.getFiles().map {
                   Route(
                       id = it.id,
                       name = it.name
                   )
               }
            )
                .display()
        )
    }
}