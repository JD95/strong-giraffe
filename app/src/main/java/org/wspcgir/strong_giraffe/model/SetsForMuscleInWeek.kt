package org.wspcgir.strong_giraffe.model

import java.time.Instant

data class SetsForMuscleInWeek(
    val range: WeekRange,
    val sets: Map<String, Int>
)