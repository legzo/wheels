package io.jterrier.wheels.database

import org.jetbrains.exposed.dao.id.IntIdTable

object RoutesTable: IntIdTable() {

    val fileId = varchar("file_id", 200)
    val name = varchar("name", 200)
    val content = blob("content")
    val url = varchar("url", 4_000)

}