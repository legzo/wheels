package io.jterrier.wheels.views

import io.jterrier.wheels.Activity
import io.jterrier.wheels.statistics.ActivityDistance
import io.jterrier.wheels.statistics.MonthlyReport
import kotlinx.html.FlowContent
import kotlinx.html.b
import kotlinx.html.div
import kotlinx.html.id

class HomeView(
    private val heatMapActivities: List<Activity>,
    private val totalNbOfActivities: Int,
    private val monthlyDistances: List<MonthlyReport>,
    private val scatterPlotData: List<ActivityDistance>,
) {

    fun display(): String = Layout.display {
        div(classes = "grid") {
            heatMap()
            monthlyDistance()
            scatterPlot()
        }
        listOfActivitiesWithMaps()
    }

    private fun FlowContent.heatMap() = div {
        h3(svg = "map", "$totalNbOfActivities rides")
        div {
            id = "heatmap"
            attributes["style"] = "width: 100%; max-width: 400px; height: 300px; margin: 1vh 0 4vh 0;"
        }
        heatmapScript(id = "heatmap", polylines = heatMapActivities.map { it.polyline })
    }

    private fun FlowContent.monthlyDistance() = div {
        h3(svg = "graph", "monthly kms")
        monthlyGraph(monthlyDistances.takeLast(13))
    }

    private fun FlowContent.scatterPlot() = div {
        h3(svg = "calendar", "history")
        scatterPlot(scatterPlotData)
    }

    private fun FlowContent.listOfActivitiesWithMaps() = div {
        h3(svg = "list", "last 10 rides")
        div(classes = "grid") {
            heatMapActivities.take(10).map { activity ->
                div {
                    div(classes = "activity") { b { +activity.name } }
                    div { +"${"%.2f".format(activity.distanceInMeters.toKm())} km" }
                    div { mapForActivity(activity) }
                }
            }
        }
    }


}