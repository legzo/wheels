package io.jterrier.wheels

import io.jterrier.wheels.controllers.DbActionsController
import io.jterrier.wheels.controllers.HomeController
import io.jterrier.wheels.controllers.RoutesListController
import io.jterrier.wheels.database.DatabaseConnector
import io.jterrier.wheels.statistics.StatsService
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("io.jterrier.wheels.Main")

private val db = DatabaseConnector()

private val stravaConnector = StravaConnector()
private val googleDriveConnector = GoogleDriveConnector()
private val statsService = StatsService()
private val mapsListController = HomeController(stravaConnector, db, statsService)
private val routesListController = RoutesListController(googleDriveConnector)
private val dbActionsController = DbActionsController(db)

val app: HttpHandler = routes(
    "/" bind GET to mapsListController::display,
    "/routes" bind GET to routesListController::display,
    "/debug" bind GET to {
        statsService.getMonthlyDistance(db.getAll())
        Response(OK)
    },
    "/db" bind routes(
        "/reset" bind GET to dbActionsController::reset
    ),
)

fun main() {
    val server = app.asServer(Jetty(9000)).start()
    logger.info("Server started on " + server.port())
}