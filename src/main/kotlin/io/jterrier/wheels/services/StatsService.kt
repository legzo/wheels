package io.jterrier.wheels.services

import io.jterrier.wheels.Activity
import io.jterrier.wheels.views.toKm
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class StatsService {

    fun getEddingtonNumber(activities: List<Activity>): Int {
        val distances = activities.map { it.distanceInMeters.toKm().toInt() }.sortedDescending()
        val indexOf = distances
            .indexOfFirst { it < distances.indexOf(it) + 1 }

        return distances[indexOf]
    }

    fun getMonthlyDistances(activities: List<Activity>): List<MonthlyReport> {
        val (thisYear, previousYear) = activities
            .groupBy { it.month() }
            .toList()
            .sortedByDescending { (month, _) -> month }
            .chunked(12)
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
        val totalKms = lastYearActivities.sumInKms()

        return YearlyStats(
            commuteKms = commuteActivities.sumInKms(),
            nonCommuteKms = nonCommuteActivities.sumInKms(),
            totalKms = totalKms,
            nbOfActivities = lastYearActivities.size,
            totalTimeOnBikeInHours = lastYearActivities.sumOf { it.durationInSeconds }.seconds.inWholeHours.toInt(),
            percentage = ((totalKms / 6000.0) * 100).toInt(),
            timePercentage = (OffsetDateTime.now().toLocalDateTime().dayOfYear / 365.0 * 100).toInt()
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
    val totalTimeOnBikeInHours: Int,
    val timePercentage: Int,
    val percentage: Int
)