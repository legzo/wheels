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
    private val yearlyStats: YearlyStats,
    private val eddingtonNumber: Int,
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
        h3(svg = "map", title = null) {
            span(classes = "mono") { +"$totalNbOfActivities" }
            span { +" rides"}
        }
        div {
            id = "heatmap"
            attributes["style"] = "width: 100%; max-width: 400px; height: 300px; margin: 1vh 0 4vh 0;"
        }
        heatmapScript(id = "heatmap", polylines = heatMapActivities.map { it.polyline })
    }

    private fun FlowContent.monthlyDistance() = div {
        h3(svg = "graph", "monthly kms")
        monthlyGraph(monthlyDistances)
    }

    private fun FlowContent.scatterPlot() = div {
        h3(svg = "calendar", "history")
        scatterPlot(scatterPlotData)
    }

    private fun FlowContent.yearly() = div {
        h3(svg = "progress", "this year so far")

        div(classes = "grid") {
            div {
                valueAndLabel(yearlyStats.totalKms, "total kms")
                div { +"${yearlyStats.commuteKms} commute" }
                div { +"${yearlyStats.nonCommuteKms} non commute" }
            }
            div {
                valueAndLabel(eddingtonNumber, "eddington number")
            }
            div {
                valueAndLabel(yearlyStats.percentage, "% of 6000 kms")
                valueAndLabel(yearlyStats.timePercentage, "% elapsed")
            }
        }

    }

    private fun FlowContent.valueAndLabel(value: Int, label: String) {
        div {
            span(classes = "value mono") { +"$value" }
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
                        span(classes = "mono") { +activity.distanceInMeters.toKm().show() }
                        span { +"km  • "}
                        span(classes = "mono") { +activity.averageSpeed.toKmPerSecond().show() }
                        span { +"km/h • "}
                        span(classes = "mono") { +"${activity.totalElevationGain}"}
                        span { +"m↑"}
                    }
                    div { mapForActivity(activity) }
                }
            }
        }
    }


}