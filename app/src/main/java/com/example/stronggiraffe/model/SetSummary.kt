package com.example.stronggiraffe.model

import com.example.stronggiraffe.model.ids.ExerciseId
import com.example.stronggiraffe.model.ids.SetId
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
