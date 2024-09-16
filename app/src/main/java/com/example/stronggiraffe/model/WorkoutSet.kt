package com.example.stronggiraffe.model

import com.example.stronggiraffe.model.ids.ExerciseId
import com.example.stronggiraffe.model.ids.LocationId
import com.example.stronggiraffe.model.ids.SetId
import java.time.Instant

data class WorkoutSet(
    val id: SetId,
    val exercise: ExerciseId,
    val location: LocationId,
    val reps: Int,
    val weight: Int,
    val time: Instant,
    val intensity: Intensity,
    val comment: String,
)
