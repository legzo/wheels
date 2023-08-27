package io.jterrier.wheels.statistics

import io.jterrier.wheels.Activity
import io.jterrier.wheels.views.toKm
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatsService {

    fun getMonthlyDistance(activities: List<Activity>): List<MonthlyReport> =
        activities
            .groupBy {
                val date: LocalDateTime =
                    it.startTime.toLocalDateTime(TimeZone.of("Europe/Paris")).toJavaLocalDateTime()
                DateTimeFormatter.ofPattern("yyyy-MM").format(date)
            }
            .map { (month, activities) ->
                MonthlyReport(
                    month = month,
                    distance = activities.sumOf { it.distanceInMeters }.toKm().toInt()
                )
            }
            .sortedBy { it.month }


    fun getScatterPlotData(activities: List<Activity>): List<ActivityDistance> =
        activities
            .sortedBy { it.startTime }
            .map {
                ActivityDistance(
                    date = it.startTime.toString(),
                    distance = it.distanceInMeters.toKm().toInt(),
                    isCommute = it.isCommute
                )
            }
}

data class MonthlyReport(
    val month: String,
    val distance: Int
)

data class ActivityDistance(
    val date: String,
    val distance: Int,
    val isCommute: Boolean
)