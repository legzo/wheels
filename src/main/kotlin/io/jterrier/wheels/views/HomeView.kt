package io.jterrier.wheels.views

import io.jterrier.wheels.Activity
import io.jterrier.wheels.services.ActivityDistance
import io.jterrier.wheels.services.MonthlyReport
import io.jterrier.wheels.services.YearlyStats
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.span

class HomeView(
    private val heatMapActivities: List<Activity>,
    private val totalNbOfActivities: Int,
    private val monthlyDistances: List<MonthlyReport>,
    private val scatterPlotData: List<ActivityDistance>,
    private val yearlyStats: YearlyStats
) {

    private val numberOfActivities = 12

    fun display(): String = Layout.display {
        div(classes = "grid") {
            heatMap()
            monthlyDistance()
            scatterPlot()
        }

        yearly()

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

    private fun FlowContent.yearly() = div {
        h3(svg = "progress", "this year so far")

        div(classes = "grid") {
            distanceAndLabel(yearlyStats.totalKms, "total")
            distanceAndLabel(yearlyStats.commuteKms, "commute")
            distanceAndLabel(yearlyStats.nonCommuteKms, "non commute")
        }

    }

    private fun FlowContent.distanceAndLabel(distance: Int, label: String) {
        div {
            span(classes = "value") { +"$distance" }
            span { +" kms" }
            span(classes = "label") { +label }
        }
    }

    private fun FlowContent.listOfActivitiesWithMaps() = div {
        h3(svg = "list", "last $numberOfActivities rides")
        div(classes = "grid") {
            heatMapActivities.take(12).map { activity ->
                div {
                    div(classes = "activity") {
                        b {
                            a(
                                href = "https://www.strava.com/activities/${activity.id}",
                                target = "_blank"
                            ) { svgIcon("strava") }
                            +activity.name
                        }
                    }
                    div(classes = "activity-details") {
                        +"${activity.distanceInMeters.toKm().show()} km • ${
                            activity.averageSpeed.toKmPerSecond().show()
                        } km/h • ${activity.totalElevationGain} m↑"
                    }
                    div { mapForActivity(activity) }
                }
            }
        }
    }


}