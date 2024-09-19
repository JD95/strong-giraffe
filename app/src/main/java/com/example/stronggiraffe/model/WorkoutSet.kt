package com.example.stronggiraffe.model

import com.example.stronggiraffe.model.ids.EquipmentId
import com.example.stronggiraffe.model.ids.ExerciseId
import com.example.stronggiraffe.model.ids.LocationId
import com.example.stronggiraffe.model.ids.SetId
import java.time.Instant

data class WorkoutSet(
    val id: SetId,
    val exercise: ExerciseId,
    val location: LocationId,
    val equipment: EquipmentId,
    val reps: Reps,
    val weight: Weight,
    val time: Instant,
    val intensity: Intensity,
    val comment: Comment,
)
