package io.jterrier.wheels.views

import io.jterrier.wheels.Route
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.div

class RoutesListView(
    private val routes: List<Route>
) {

    fun display(): String = Layout.display {
        listOfRoutes()
    }

    private fun FlowContent.listOfRoutes() = div {
        h3(svg = "route", "${routes.size} routes")
        div(classes = "grid") {
            routes.map { route ->
                div {
                    div(classes = "route") {
                        a(href = route.url, target = "_blank") { +"✎" }
                        +" "
                        b { +route.name }
                    }
                }
            }
        }
    }


}