package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.MuscleId

data class SetsForMuscleInWeek(
    val range: WeekRange,
    val setCounts: Map<MuscleId, Pair<String,Int>>
)