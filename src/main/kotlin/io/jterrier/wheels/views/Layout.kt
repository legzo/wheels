package io.jterrier.wheels.views

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.title

object Layout {

    inline fun display(crossinline block: FlowContent.() -> Unit = {}) = createHTML().html {

        head {

            title("Wheels")
            meta(name = "viewport", content = "width=device-width,initial-scale=1") { }
            meta(charset = "UTF-8") { }

            link(
                rel = "icon",
                href = "data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🚴</text></svg>"
            )
            script(
                src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
            ) {}
            script(
                src = "https://unpkg.com/htmx.org@1.9.5",
            ) {
                attributes["crossorigin"] = "anonymous"
            }
            script(
                type = "text/javascript",
                src = "https://rawgit.com/jieter/Leaflet.encoded/master/Polyline.encoded.js"
            ) {}
            script(
                type = "text/javascript",
                src = "https://cdnjs.cloudflare.com/ajax/libs/leaflet-gpx/1.7.0/gpx.min.js"
            ) {}

            link(
                rel = "stylesheet",
                href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
            ) {}

            inlineCssFromFile("styles.css")
        }

        body {
            div(classes = "region") {
                h1("bike", "Wheels")
                div {
                    id = "navigation"

                    a {
                        href = "/"
                        +"activities"
                    }
                    a {
                        href = "/routes"
                        +"routes"
                    }
                }
                block()
            }
        }
    }

}