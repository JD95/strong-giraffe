package org.wspcgir.strong_giraffe.repository.entity.set

import androidx.room.DatabaseView

@DatabaseView(
    """
        SELECT workout_set.id
             , exercise.name as exerciseName
             , exercise.id as exerciseId
             , workout_set.reps
             , workout_set.weight
             , workout_set.time
             , workout_set.intensity
        FROM workout_set
          JOIN exercise on exercise.id = workout_set.exercise
    """
)
data class SetSummary(
    val id: String,
    val exerciseName: String,
    val exerciseId: String,
    val reps: Int,
    val weight: Float,
    val time: Long,
    val intensity: Int,
)
