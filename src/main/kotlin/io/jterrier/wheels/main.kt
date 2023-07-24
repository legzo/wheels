package io.jterrier.wheels

import io.jterrier.wheels.controllers.DbActionsController
import io.jterrier.wheels.controllers.MapsListController
import io.jterrier.wheels.database.DatabaseConnector
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("io.jterrier.wheels.Main")

private val db = DatabaseConnector()

private val stravaConnector = StravaConnector()
private val mapsListController = MapsListController(stravaConnector, db)
private val dbActionsController = DbActionsController(db)

val app: HttpHandler = routes(
    "/" bind GET to mapsListController::displayMaps,
    "/db" bind routes(
        "/reset" bind GET to dbActionsController::reset
    ),
)

fun main() {
    val server = app.asServer(Jetty(9000)).start()
    logger.info("Server started on " + server.port())
}