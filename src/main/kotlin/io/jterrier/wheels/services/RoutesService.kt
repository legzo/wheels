package io.jterrier.wheels.services

import io.jterrier.wheels.GoogleDriveConnector
import io.jterrier.wheels.Route
import io.jterrier.wheels.RouteWithGpx
import io.jterrier.wheels.database.DatabaseConnector
import io.jterrier.wheels.launchChunked
import io.jterrier.wheels.parseWithRegex
import kotlinx.coroutines.Dispatchers

class RoutesService(
    private val googleDriveConnector: GoogleDriveConnector,
    private val db: DatabaseConnector,
) {

    fun getRouteWithGpx(id: String): RouteWithGpx? {
        return db.getRouteWithGpx(id)
    }

    fun getRoutes(): List<Route> {
        val alreadySavedIds = db.getAllRoutesIds()

        val newFilesToFetch = googleDriveConnector
            .getFiles()
            .filter { it.id !in alreadySavedIds }

        val newRoutes = newFilesToFetch
            .launchChunked(Dispatchers.IO, 10) {
                val routeGpx = googleDriveConnector.getFile(it.id)
                RouteWithGpx(
                    route = Route(
                        id = it.id,
                        name = it.name.replace(".gpx", ""),
                        url = findUrl(routeGpx)
                    ),
                    gpx = routeGpx
                )
            }

        db.saveRoutes(newRoutes)

        return db.getAllRoutes()
    }

    companion object {
        fun findUrl(routeGpx: String) =
            routeGpx.parseWithRegex("href=\"(.+)\"") { (url) ->
                url.replace("&amp;", "&")
            } ?: ""
    }
}