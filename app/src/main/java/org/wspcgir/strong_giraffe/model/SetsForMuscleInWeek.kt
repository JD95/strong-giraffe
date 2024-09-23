package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.MuscleId


data class MuscleSetHistory (
    val name: String,
    val thisWeek: Int,
    val lastWeek: Int,
)

data class SetsForMuscleInWeek(
    val range: WeekRange,
    val setCounts: Map<MuscleId, MuscleSetHistory>
)