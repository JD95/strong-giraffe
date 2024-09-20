package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.MuscleId

data class Exercise(
    val id: ExerciseId,
    val name: String,
    val muscle: MuscleId,
)
