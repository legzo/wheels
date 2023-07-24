package io.jterrier.wheels

import io.jterrier.wheels.views.toKm
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatsService {

    fun getMonthlyDistance(activities: List<Activity>): List<Pair<String, Int>> =
        activities
            .groupBy {
                val date: LocalDateTime =
                    it.startDate.toLocalDateTime(TimeZone.of("Europe/Paris")).toJavaLocalDateTime()
                DateTimeFormatter.ofPattern("yyyy-MM").format(date)
            }
            .mapValues { (_, activities) -> activities.sumOf { it.distanceInMeters }.toKm().toInt() }
            .toList()
            .sortedBy { it.first }

}