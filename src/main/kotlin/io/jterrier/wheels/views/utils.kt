package io.jterrier.wheels.views

fun Double.toKm(): Double = this / 1000

fun Double.toKmPerSecond(): Double = this / 1000 * 60 * 60

fun Double.show() = "%.2f".format(this)
