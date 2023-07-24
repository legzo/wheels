package io.jterrier.wheels.views

import io.jterrier.wheels.Activity
import kotlinx.html.FlowContent
import kotlinx.html.FlowOrHeadingContent
import kotlinx.html.FlowOrMetaDataOrPhrasingContent
import kotlinx.html.H1
import kotlinx.html.H3
import kotlinx.html.HEAD
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.attributesMapOf
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.unsafe
import kotlinx.html.visit

fun HEAD.inlineCssFromFile(fileName: String) =
    style {
        unsafe {
            raw(getResourceAsText("/$fileName"))
        }
    }


fun FlowContent.mapForActivity(it: Activity) {
    div {
        id = "map-${it.id}"
        attributes["style"] = "width: 100%; max-width: 400px; height: 300px; margin: 1vh 0 4vh 0;"
    }

    mapScript(it.id, it.polyline)
}

fun String.rectify() = replace("""\""", """\\""")

fun FlowOrMetaDataOrPhrasingContent.mapScript(id: Long, polyline: String) =
    mapScript(id.toString(), polyline)

fun FlowOrMetaDataOrPhrasingContent.mapScript(id: String, polyline: String) =
    script {
        unsafe {
            raw(
                """
                (function () {
                  const map = L.map('map-$id');
                  L.tileLayer(
                    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                  }).addTo(map);

                  const encoded = "${polyline.rectify()}"
                    
                  try {  
                  const coordinates = L.Polyline.fromEncoded(encoded).getLatLngs();
                  const poly = L.polyline(
                    coordinates,
                    {
                      color: '#0042bf',
                      weight: 4,
                      opacity: .7,
                      lineJoin: 'round'
                    }
                  ).addTo(map);

                  map.fitBounds(poly.getBounds());
                  } catch (error){
                    console.log(error)
                  }
                })()
                """.trimIndent()
            )
        }
    }

fun FlowOrMetaDataOrPhrasingContent.heatmapScript(id: String, polylines: List<String>) =
    script {
        unsafe {
            val polylinesForJs = polylines.joinToString(prefix = "[", postfix = "]") { "'${it.rectify()}'" }

            raw(
                """
                (function () {
                  const map = L.map('$id', {
                    center: [44.842, -0.551],
                    zoom: 10
                  });
                  
                  L.tileLayer(
                    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                  }).addTo(map);
                
                  const encodedRoutes = $polylinesForJs
                
                  try {
                
                    const globalBounds = L.polyline(L.Polyline.fromEncoded(encodedRoutes[0]).getLatLngs()).getBounds()
                
                    for (const encoded of encodedRoutes) {
                
                      const coordinates = L.Polyline.fromEncoded(encoded).getLatLngs();
                      const poly = L.polyline(
                        coordinates,
                        {
                          color: '#0042bf',
                          weight: 4,
                          opacity: .4,
                          lineJoin: 'round'
                        }
                      ).addTo(map);
                      globalBounds.extend(poly.getBounds())
                    }
                
                    //map.fitBounds(globalBounds);
                
                  } catch (error) {
                    console.log(error)
                  }
                })()
                """.trimIndent()
            )
        }
    }

fun HTMLTag.svgFromFile(name: String) =
    unsafe {
        raw(getResourceAsText("/svg/$name.svg"))
    }

fun FlowContent.monthlyGraph(monthlyDistances: List<Pair<String, Int>>) {
    div {
        id = "monthly-distances"
    }

    val monthlyDistancesJson = monthlyDistances
        .takeLast(13)
        .joinToString(prefix = "[", postfix = "]") { "{ month: '${it.first}', distance: ${it.second} }" }

    script(type = "module") {
        unsafe {
            raw(
                """
                import * as Plot from "https://cdn.jsdelivr.net/npm/@observablehq/plot@0.6/+esm";
                
                const data = $monthlyDistancesJson
                
                const plot =
                  Plot
                    .barY(
                      data,
                      {
                        x: "month",
                        y: "distance",
                        fill: "#555"
                      },
                    )
                    .plot(
                      {
                        x: {
                          type: "band"
                        },
                        y: {
                          grid: true
                        },
                        style: {
                          background: "var(--color-light)"
                        }
                      }
                    );
                const div = document.querySelector("#monthly-distances");
                div.append(plot);
                 """.trimIndent()
            )
        }
    }
}

@HtmlTagMarker
inline fun FlowOrHeadingContent.h1(svg: String, title: String, crossinline block: H1.() -> Unit = {}): Unit =
    H1(attributesMapOf("class", null), consumer).visit {
        title(svg, title)
        block()
    }

@HtmlTagMarker
inline fun FlowOrHeadingContent.h3(svg: String, title: String, crossinline block: H3.() -> Unit = {}): Unit =
    H3(attributesMapOf("class", null), consumer).visit {
        title(svg, title)
        block()
    }

fun HTMLTag.title(svg: String, title: String) {
    svgFromFile(svg)
    +title
}

fun getResourceAsText(path: String): String =
    object {}.javaClass.getResource(path)?.readText().orEmpty()

