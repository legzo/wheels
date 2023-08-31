package io.jterrier.wheels

import io.jterrier.wheels.controllers.DbActionsController
import io.jterrier.wheels.controllers.HomeController
import io.jterrier.wheels.controllers.RoutesListController
import io.jterrier.wheels.database.DatabaseConnector
import io.jterrier.wheels.services.ActivitiesService
import io.jterrier.wheels.services.RoutesService
import io.jterrier.wheels.services.StatsService
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("io.jterrier.wheels.Main")

private val db = DatabaseConnector()

private val stravaConnector = StravaConnector()
private val googleDriveConnector = GoogleDriveConnector()

private val statsService = StatsService()
private val activitiesService = ActivitiesService(stravaConnector, db)
private val routesService = RoutesService(googleDriveConnector, db)

private val mapsListController = HomeController(activitiesService, statsService)
private val routesListController = RoutesListController(routesService)
private val dbActionsController = DbActionsController(db)

val app: HttpHandler = routes(
    "/" bind GET to mapsListController::display,
    "/route" bind GET to routesListController::routeDetails,
    "/routes" bind GET to routesListController::display,
    "/gpx" bind GET to routesListController::gpx,
    "/debug" bind GET to {
        Response(OK)
    },
    "/db" bind routes(
        "/reset" bind GET to dbActionsController::reset
    ),
    static(ResourceLoader.Classpath("public"))
)

fun main() {
    val server = app.asServer(Jetty(9000)).start()
    logger.info("Server started on " + server.port())
}