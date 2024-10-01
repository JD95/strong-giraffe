package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.SetId
import java.time.Instant
import java.time.OffsetDateTime

data class SetSummary(
    val id: SetId,
    val exerciseName: String,
    val exerciseId: ExerciseId,
    val reps: Reps,
    val weight: Weight,
    val time: OffsetDateTime,
    val intensity: Intensity,
)
