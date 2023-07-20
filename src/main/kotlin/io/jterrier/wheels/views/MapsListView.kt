package io.jterrier.wheels.views

import io.jterrier.wheels.Activity
import io.jterrier.wheels.h3
import io.jterrier.wheels.mapScript
import kotlinx.html.b
import kotlinx.html.div
import kotlinx.html.id

class MapsListView(
    private val activities: List<Activity>,
    private val minDistanceInKm: Int
) {

    fun display(): String = Layout.display {

        h3(svg = "check", "${activities.size} last rides > $minDistanceInKm kms")
        activities.map {
            div {
                div {
                    b { +it.name }
                }
                div {
                    +"${"%.2f".format(it.distance)} km"
                }

                div {
                    div {
                        id = "map-${it.id}"
                        attributes["style"] = "width: 100%; max-width: 400px; height: 300px; margin: 1vh 0 4vh 0;"
                    }

                    mapScript(it.id, it.polyline.replace("""\""", """\\"""))
                }
            }
        }
    }

}