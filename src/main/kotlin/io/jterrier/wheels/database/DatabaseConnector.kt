package io.jterrier.wheels.database

import io.jterrier.wheels.Activity
import io.jterrier.wheels.databaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
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
        }
    }

    fun save(activities: Collection<Activity>) = transaction {
        ActivitiesTable
            .batchInsert(activities, shouldReturnGeneratedValues = false) {
                this[ActivitiesTable.stravaId] = it.id
                this[ActivitiesTable.name] = it.name
                this[ActivitiesTable.distance] = it.distance
                this[ActivitiesTable.polyline] = it.polyline
            }
    }

    fun getAll(): List<Activity> = transaction {
        ActivitiesTable
            .selectAll()
            .map {
                Activity(
                    id = it[ActivitiesTable.stravaId],
                    name = it[ActivitiesTable.name],
                    distance = it[ActivitiesTable.distance],
                    polyline = it[ActivitiesTable.polyline],
                )
            }
    }
}