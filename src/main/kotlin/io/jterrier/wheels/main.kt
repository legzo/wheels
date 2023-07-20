package io.jterrier.wheels

import io.jterrier.wheels.controllers.MapsListController
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("io.jterrier.wheels.Main")

private val stravaConnector = StravaConnector()
private val mapsListController = MapsListController(stravaConnector)


val app: HttpHandler = routes(
    "/" bind GET to mapsListController::displayMaps,
)

fun Float.toKm(): Double = this.toDouble() / 1000

fun main() {
    val server = app.asServer(Jetty(9000)).start()
    logger.info("Server started on " + server.port())
}