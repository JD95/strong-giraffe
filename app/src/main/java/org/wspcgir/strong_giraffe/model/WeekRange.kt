package org.wspcgir.strong_giraffe.model

import java.time.Instant
import java.time.OffsetDateTime
import java.util.*


data class WeekRange(
    val start: OffsetDateTime,
    val end: OffsetDateTime,
) {

    companion object {
        fun forInstant(now: Instant, zone: TimeZone): WeekRange {
            val tz = TimeZone.getDefault()
            val time = OffsetDateTime.ofInstant(Instant.now(), tz.toZoneId())
            val startOfWeek = time
                .minusDays(time.dayOfWeek.value.toLong() % 7)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
            val endOfWeek = startOfWeek.plusWeeks(1)
            return WeekRange(startOfWeek, endOfWeek)
        }
    }
}

