package io.jterrier.wheels.database

import io.jterrier.wheels.Activity
import io.jterrier.wheels.Route
import io.jterrier.wheels.databaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector {

    init {
        Database.connect(
            url = databaseConfig.url.value,
            driver = "org.postgresql.Driver",
            user = databaseConfig.user.value,
            password = databaseConfig.password.value,
        )

        transaction {
            // print sql to std-out
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(ActivitiesTable)
            SchemaUtils.create(RoutesTable)
        }
    }

    fun saveActivities(activities: Collection<Activity>) = transaction {
        ActivitiesTable
            .batchInsert(activities, shouldReturnGeneratedValues = false) {
                this[ActivitiesTable.stravaId] = it.id
                this[ActivitiesTable.name] = it.name
                this[ActivitiesTable.distance] = it.distanceInMeters
                this[ActivitiesTable.durationInSeconds] = it.durationInSeconds
                this[ActivitiesTable.totalElevationGain] = it.totalElevationGain
                this[ActivitiesTable.startTime] = it.startTime
                this[ActivitiesTable.averageSpeed] = it.averageSpeed
                this[ActivitiesTable.maxSpeed] = it.maxSpeed
                this[ActivitiesTable.polyline] = it.polyline
                this[ActivitiesTable.isCommute] = it.isCommute
            }
    }

    fun saveRoutes(routes: Collection<Route>) = transaction {
        RoutesTable
            .batchInsert(routes, shouldReturnGeneratedValues = false) {
                this[RoutesTable.fileId] = it.id
                this[RoutesTable.name] = it.name
                this[RoutesTable.url] = it.url
                this[RoutesTable.content] = ExposedBlob(it.content.encodeToByteArray())
            }
    }

    fun getAllActivities(): List<Activity> = transaction {
        ActivitiesTable
            .selectAll()
            .map {
                Activity(
                    id = it[ActivitiesTable.stravaId],
                    name = it[ActivitiesTable.name],
                    distanceInMeters = it[ActivitiesTable.distance],
                    durationInSeconds = it[ActivitiesTable.durationInSeconds],
                    totalElevationGain = it[ActivitiesTable.totalElevationGain],
                    startTime = it[ActivitiesTable.startTime],
                    averageSpeed = it[ActivitiesTable.averageSpeed],
                    maxSpeed = it[ActivitiesTable.maxSpeed],
                    polyline = it[ActivitiesTable.polyline],
                    isCommute = it[ActivitiesTable.isCommute],
                )
            }
    }

    fun getAllRoutes(): List<Route> = transaction {
        RoutesTable
            .selectAll()
            .map {
                Route(
                    id = it[RoutesTable.fileId],
                    name = it[RoutesTable.name],
                    content = it[RoutesTable.content].toString(),
                    url = it[RoutesTable.url],
                )
            }
    }

    fun resetTable() = transaction {
        SchemaUtils.drop(ActivitiesTable)
        SchemaUtils.create(ActivitiesTable)
    }
}