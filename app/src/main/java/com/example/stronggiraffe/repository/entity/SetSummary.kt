package com.example.stronggiraffe.repository.entity

import androidx.room.DatabaseView
import com.example.stronggiraffe.model.Intensity
import com.example.stronggiraffe.model.Reps
import com.example.stronggiraffe.model.Weight
import com.example.stronggiraffe.model.ids.ExerciseId
import com.example.stronggiraffe.model.ids.SetId
import java.time.Instant

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
    val weight: Int,
    val time: Long,
    val intensity: Int,
)
