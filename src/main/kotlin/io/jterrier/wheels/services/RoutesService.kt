package io.jterrier.wheels.services

import io.jterrier.wheels.GoogleDriveConnector
import io.jterrier.wheels.Route
import io.jterrier.wheels.RouteWithGpx
import io.jterrier.wheels.database.DatabaseConnector
import io.jterrier.wheels.launchChunked
import io.jterrier.wheels.parseWithRegex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Instant

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
            .take(40)

        val newRoutes = newFilesToFetch
            .launchChunked(Dispatchers.IO, 10) {
                coroutineScope {

                    val routeGpx = async { googleDriveConnector.getFileContents(it.id) }
                    val lastModified = async { googleDriveConnector.getFileModifiedTime(it.id) }

                    RouteWithGpx(
                        route = Route(
                            id = it.id,
                            name = it.name.replace(".gpx", ""),
                            url = findUrl(routeGpx.await()),
                            lastModified = Instant.parse(lastModified.await())
                        ),
                        gpx = routeGpx.await()
                    )

                }
            }

        db.saveRoutes(newRoutes)

        return db.getAllRoutes().sortedByDescending { it.lastModified }
    }

    companion object {
        fun findUrl(routeGpx: String) =
            routeGpx.parseWithRegex("href=\"(.+)\"") { (url) ->
                url.replace("&amp;", "&")
            } ?: ""
    }
}