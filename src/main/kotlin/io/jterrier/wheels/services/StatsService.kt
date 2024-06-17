package io.jterrier.wheels.services

import io.jterrier.wheels.Activity
import io.jterrier.wheels.views.toKm
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class StatsService {

    fun getMonthlyDistances(activities: List<Activity>): List<MonthlyReport> {
        val (thisYear, previousYear) = activities
            .groupBy { it.month() }
            .toList()
            .sortedByDescending { (month, _) -> month }
            .chunked(13)
            .take(2)

        return thisYear
            .zip(previousYear)
            .map { (current, previous) ->
                MonthlyReport(
                    month = current.first,
                    distance = current.second.sumInKms(),
                    distanceVsPreviousYear = current.second.sumInKms() - previous.second.sumInKms()
                )
            }
    }

    private fun Activity.month(): String {
        val date = startTime.toLocalDateTime(TimeZone.of("Europe/Paris")).toJavaLocalDateTime()
        return DateTimeFormatter.ofPattern("yyyy-MM").format(date)
    }

    private fun List<Activity>.sumInKms() =
        sumOf { it.distanceInMeters }.toKm().toInt()


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

    fun getYearlyStats(activities: List<Activity>): YearlyStats {
        val lastYearActivities = activities.filter { it.isInCurrentYear() }

        val (commuteActivities, nonCommuteActivities) = lastYearActivities.partition { it.isCommute }

        return YearlyStats(
            commuteKms = commuteActivities.sumInKms(),
            nonCommuteKms = nonCommuteActivities.sumInKms(),
            totalKms = lastYearActivities.sumInKms(),
            nbOfActivities = lastYearActivities.size,
            totalTimeOnBikeInHours = lastYearActivities.sumOf { it.durationInSeconds }.seconds.inWholeHours.toInt()
        )
    }
}

data class MonthlyReport(
    val month: String,
    val distance: Int,
    val distanceVsPreviousYear: Int,
)

data class ActivityDistance(
    val date: String,
    val distance: Int,
    val isCommute: Boolean
)

data class YearlyStats(
    val commuteKms: Int,
    val nonCommuteKms: Int,
    val totalKms: Int,
    val nbOfActivities: Int,
    val totalTimeOnBikeInHours: Int
)