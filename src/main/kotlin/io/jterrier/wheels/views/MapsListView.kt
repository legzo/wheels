package io.jterrier.wheels.views

import io.jterrier.wheels.Activity
import io.jterrier.wheels.h3
import io.jterrier.wheels.heatmapScript
import io.jterrier.wheels.mapForActivity
import kotlinx.html.b
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.id

class MapsListView(
    private val activities: List<Activity>,
    private val minDistanceInKm: Int,
    private val totalNbOfActivities: Int,
) {

    fun display(): String = Layout.display {

        h2 { +"${activities.size} rides > $minDistanceInKm kms (of $totalNbOfActivities)" }

        h3(svg = "map", "Heatmap")

        div {
            id = "heatmap"
            attributes["style"] = "width: 100%; max-width: 400px; height: 300px; margin: 1vh 0 4vh 0;"
        }
        heatmapScript(id = "heatmap", polylines = activities.map { it.polyline })

        h3(svg = "list", "List")

        div(classes = "grid") {
            activities.map { activity ->
                div {
                    div { b { +activity.name } }
                    div { +"${"%.2f".format(activity.distance.toKm())} km" }
                    div { mapForActivity(activity) }
                }
            }
        }
    }


}