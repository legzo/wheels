package io.jterrier.wheels.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object RoutesTable: IntIdTable() {

    val fileId = varchar("file_id", 200)
    val name = varchar("name", 200)
    val content = blob("content")
    val url = varchar("url", 4_000)
    val lastModified =  timestamp("last_modified").nullable()

}