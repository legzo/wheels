package io.jterrier.wheels.views

import io.jterrier.wheels.Route
import kotlinx.html.FlowContent
import kotlinx.html.b
import kotlinx.html.div

class RoutesListView(
    private val routes: List<Route>
) {

    fun display(): String = Layout.display {
        listOfRoutes()
    }

    private fun FlowContent.listOfRoutes() = div {
        h3(svg = "list", "routes")
        div(classes = "grid") {
            routes.map { route ->
                div {
                    div(classes = "route") { b { +route.name } }
                }
            }
        }
    }


}