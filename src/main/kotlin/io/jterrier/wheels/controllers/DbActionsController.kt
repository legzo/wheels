package io.jterrier.wheels.controllers

import io.jterrier.wheels.database.DatabaseConnector
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DbActionsController(
    private val dbConnector: DatabaseConnector,
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun reset(@Suppress("UNUSED_PARAMETER") request: Request): Response {
        logger.info("Resetting table `activities`")
        dbConnector.reset()
        return Response(Status.OK)
    }

}