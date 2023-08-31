package io.jterrier.wheels.views

import io.jterrier.wheels.Route
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.stream.createHTML

class RoutesListView(
    private val routes: List<Route>
) {

    fun display(): String = Layout.display {
        listOfRoutes()
    }

    private fun FlowContent.listOfRoutes() = div {
        h3(svg = "route", "${routes.size} routes")
        div(classes = "grid") {
            routes.mapIndexed { index, it ->
                if (index < 12) routeDivDetails(it) else routeDiv(it)
            }
        }
    }

    private fun DIV.routeDiv(route: Route) {
        div {
            val routeId = "route-${route.id}"
            div(classes = "route") {
                id = routeId
                a(href = route.url, target = "_blank") { svgIcon("edit") }
                +" "
                a(href = "#") {
                    attributes["hx-get"] = "/route?id=${route.id}"
                    attributes["hx-target"] = "#$routeId"
                    attributes["hx-swap"] = "outerHTML"
                    svgIcon("view")
                }
                +" "
                b { +route.name }
            }
        }
    }

    companion object {
        private fun DIV.routeDivDetails(route: Route) {
            div {
                div(classes = "route") {
                    a(href = route.url, target = "_blank") {
                        svgIcon("edit")
                    }
                    +" "
                    b { +route.name }

                    mapForRoute(route)
                }
            }
        }

        fun details(route: Route): String =  createHTML().div {
            routeDivDetails(route)
        }
    }

}