package io.jterrier.wheels

import kotlinx.html.FlowOrHeadingContent
import kotlinx.html.FlowOrMetaDataOrPhrasingContent
import kotlinx.html.H3
import kotlinx.html.HEAD
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.attributesMapOf
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

fun FlowOrMetaDataOrPhrasingContent.mapScript(id: Long, polyline: String) =
    script {
        unsafe {
            raw(
                """
                (function () {
                  const map = L.map('map-$id');
                  L.tileLayer(
                    'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                  }).addTo(map);

                  const encoded = "$polyline"
                    
                  try {  
                  const coordinates = L.Polyline.fromEncoded(encoded).getLatLngs();
                  const poly = L.polyline(
                    coordinates,
                    {
                      color: 'blue',
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

fun HTMLTag.svgFromFile(name: String) =
    unsafe {
        raw(getResourceAsText("/svg/$name.svg"))
    }

@HtmlTagMarker
inline fun FlowOrHeadingContent.h3(svg: String, title: String, crossinline block: H3.() -> Unit = {}): Unit =
    H3(attributesMapOf("class", null), consumer).visit {
        title(svg, title)
        block()
    }

fun HTMLTag.title(svg: String, title: String) {
    svgFromFile(svg)
    +" $title"
}

fun getResourceAsText(path: String): String =
    object {}.javaClass.getResource(path)?.readText().orEmpty()

