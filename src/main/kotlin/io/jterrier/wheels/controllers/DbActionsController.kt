package io.jterrier.wheels.controllers

import io.jterrier.wheels.database.DatabaseConnector
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DbActionsController(
    private val db: DatabaseConnector,
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun reset(@Suppress("UNUSED_PARAMETER") request: Request): Response {
        logger.info("Resetting table `routes`")
        db.resetRoutes()
        return Response(Status.OK)
    }

}