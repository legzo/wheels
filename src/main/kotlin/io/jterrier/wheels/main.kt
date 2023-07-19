package io.jterrier.wheels

import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.stream.createHTML
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger("io.jterrier.wheels.Main")


val app: HttpHandler = routes(
    "/html" bind GET to {
        Response(OK).body(createHTML().html {

            head {
                meta(charset = "UTF-8") { }
                link(
                    rel = "icon",
                    href = "data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🚴</text></svg>"
                )
                inlineCssFromFile("styles.css")
            }

            body {
                div(classes = "region") {
                    h1 { +"Wheels" }
                    h3(svg = "check", "Recent rides")
                }
            }
        })
    },
    "/api" bind routes(
        "/test" bind GET to { _ -> Response(OK).body(File(".").absolutePath) }
    )
)

fun main() {
    val server = app.asServer(Jetty(9000)).start()
    logger.info("Server started on " + server.port())
}