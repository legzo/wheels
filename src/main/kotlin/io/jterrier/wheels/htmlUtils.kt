package io.jterrier.wheels

import kotlinx.html.FlowOrHeadingContent
import kotlinx.html.H3
import kotlinx.html.HEAD
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.attributesMapOf
import kotlinx.html.style
import kotlinx.html.unsafe
import kotlinx.html.visit


fun HEAD.inlineCssFromFile(fileName: String) =
    style {
        unsafe {
            raw(getResourceAsText("/$fileName"))
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

