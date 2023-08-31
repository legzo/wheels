package io.jterrier.wheels.views

import io.jterrier.wheels.Activity
import io.jterrier.wheels.Route
import io.jterrier.wheels.services.ActivityDistance
import io.jterrier.wheels.services.MonthlyReport
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

private const val graphHeight = 320

fun FlowContent.mapForActivity(it: Activity) {
    div {
        id = "map-${it.id}"
        attributes["style"] = "width: 100%; max-width: 400px; height: ${graphHeight}px; margin: 1vh 0 4vh 0;"
    }

    mapScript(it.id, it.polyline)
}

fun String.rectify() = replace("""\""", """\\""")

fun FlowContent.mapForRoute(it: Route) {
    div {
        id = "route-map-${it.id}"
        attributes["style"] = "width: 100%; max-width: 400px; height: ${graphHeight}px; margin: 1vh 0 4vh 0;"
    }
    script {
        unsafe {
            raw(
                """
                (function () {
                  const map = L.map('route-map-${it.id}', {
                    zoomControl: false,
                    scrollWheelZoom: false,
                    touchZoom: false,
                    dragging: false,
                    zoomSnap: 0.3,
                  });
                  L.tileLayer(
                    // 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',  
                    'https://tile.osmand.net/hd/{z}/{x}/{y}.png',  
                    {
                      maxZoom: 18,
                    }
                  ).addTo(map);
    
                  var gpx = '/gpx?id=${it.id}'; // URL to your GPX file or the GPX itself
                  new L.GPX(gpx, {
                    async: true,
                    polyline_options: {
                      color: '#0042bf',
                      weight: 4,
                      opacity: .7,
                      lineJoin: 'round'
                    },
                    marker_options: {
                      startIconUrl: '/marker.png',
                      endIconUrl: '/marker.png',
                      shadowUrl: '/marker.png',
                      wptIconUrls: {
                        '': '/marker.png',
                      }
                    }
                  }).on('loaded', function(e) {
                    map.fitBounds(e.target.getBounds());
                  }).addTo(map);
                  
                })()
                """.trimIndent()
            )
        }
    }
}

fun FlowOrMetaDataOrPhrasingContent.mapScript(id: Long, polyline: String) =
    mapScript(id.toString(), polyline)

fun FlowOrMetaDataOrPhrasingContent.mapScript(id: String, polyline: String) =
    script {
        unsafe {
            raw(
                """
                (function () {
                  const map = L.map('map-$id', {
                    zoomControl: false,
                    scrollWheelZoom: false,
                    touchZoom: false,
                    dragging: false,
                    zoomSnap: 0.3,
                  });
                  L.tileLayer(
                    // 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',  
                    'https://tile.osmand.net/hd/{z}/{x}/{y}.png',  
                    {
                      maxZoom: 18,
                    }
                  ).addTo(map);

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
                    zoom: 10,
                  });
                  
                  L.tileLayer(
                    // 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                    'https://tile.osmand.net/hd/{z}/{x}/{y}.png',
                    {
                      maxZoom: 18,
                    }
                  ).addTo(map);
                
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

fun FlowContent.monthlyGraph(monthlyDistances: List<MonthlyReport>) {
    div {
        id = "monthly-distances"
    }

    val monthlyDistancesJson = monthlyDistances
        .joinToString(prefix = "[", postfix = "]") { "{ month: new Date('${it.month}'), distance: ${it.distance} }" }

    script(type = "module") {
        unsafe {
            raw(
                """
                import * as Plot from "https://cdn.jsdelivr.net/npm/@observablehq/plot@0.6/+esm";
                
                const data = $monthlyDistancesJson
                
                 const plot =
                  Plot
                    .plot(
                      {
                        x: {
                          grid: true,
                          label: null,
                          ticks: 12,
                        },
                        y: {
                          grid: true,
                          label: null,
                          labelAnchor: "top",
                          domain: [0, 700],
                          ticks: 6,
                        },
                        style: {
                          background: "var(--color-light)",
                          height: "${graphHeight}px"
                        },
                        marks: [
                          Plot.lineY(
                            data,
                            {
                              x: "month",
                              y: "distance",
                              stroke: "#555",
                              strokeWidth: 4
                            },
                          ),
                          Plot.areaY(
                            data,
                            {
                              x: "month",
                              y: "distance",
                              fillOpacity: 0.1
                            }
                          ),
                          Plot.dot(
                            data,
                            {
                              x: "month",
                              y: "distance",
                              r: 6,
                              fill: "#0042bf",
                              tip: true,
                            },                            
                          ),
                          Plot.dot(
                            data,
                            {
                              x: "month",
                              y: "distance",
                              r: 8,
                              stroke: "#0042bf",
                            },                            
                          )
                        ]
                      }
                    )
                  ;
                    
                const div = document.querySelector("#monthly-distances");
                div.append(plot);
                 """.trimIndent()
            )
        }
    }
}

fun FlowContent.scatterPlot(activitiesByDate: List<ActivityDistance>) {
    div {
        id = "activities-by-date"
    }

    val activitiesByDateJson = activitiesByDate
        .joinToString(prefix = "[", postfix = "]") {
            "{ date: new Date('${it.date}'), distance: ${it.distance}, isCommute: ${it.isCommute} }"
        }

    script(type = "module") {
        unsafe {
            raw(
                """
                import * as Plot from "https://cdn.jsdelivr.net/npm/@observablehq/plot@0.6/+esm";
                
                const data = $activitiesByDateJson
               
                const plot =
                  Plot
                    .dot(
                      data,
                      {
                        x: "date",
                        y: "distance",
                        r: 5,
                        fill: "#0042bf",
                        // fill: (d) => d.isCommute ? "#555555" : "#0042bf",
                        tip: true,
                      },
                    )
                    .plot(
                      {
                        style: {
                          background: "var(--color-light)",
                          height: "${graphHeight}px"
                        },
                        y: {
                          grid: true,
                          ticks: 6,
                          label: null,
                          domain: [0, 130],
                        },
                      }
                    );
               
                const div = document.querySelector("#activities-by-date");
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

