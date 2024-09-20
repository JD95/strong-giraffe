package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.SetId
import java.time.Instant

data class SetSummary(
    val id: SetId,
    val exerciseName: String,
    val exerciseId: ExerciseId,
    val reps: Reps,
    val weight: Weight,
    val time: Instant,
    val intensity: Intensity,
)
