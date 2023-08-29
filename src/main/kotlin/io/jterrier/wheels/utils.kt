package io.jterrier.wheels

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.time.measureTimedValue

val parallelizationLogger: Logger =
    LoggerFactory.getLogger((object : Any() {}).javaClass.enclosingClass)

fun <T : Any, U> Collection<U>.launchChunked(
    dispatcher: CoroutineDispatcher,
    chunkSize: Int,
    todo: suspend (U) -> T?,
): List<T> {
    val (result, time) = measureTimedValue {
        runBlocking(dispatcher) {
            chunked(chunkSize)
                .mapIndexed { index, chunk ->

                    val chunkResults = chunk.map {
                        async { todo(it) }
                    }.awaitAll()


                    val todoCount = max(size - (index + 1) * chunkSize, 0)
                    parallelizationLogger.info("Remaining : $todoCount / $size")

                    chunkResults
                }.flatten()
                .filterNotNull()
        }
    }

    parallelizationLogger.info("All $size done in ${time.inWholeMilliseconds}ms")
    return result
}

fun <T> String.parseWithRegex(
    regex: String,
    mapResult: (MatchResult.Destructured) -> T?
): T? {
    val matchResult = Regex(regex).find(this)
    return if (matchResult != null) {
        mapResult(matchResult.destructured)
    } else null
}