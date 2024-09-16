package com.example.stronggiraffe.model

import com.example.stronggiraffe.model.ids.ExerciseId
import com.example.stronggiraffe.model.ids.MuscleId

data class Exercise(
    val id: ExerciseId,
    val name: String,
    val muscle: MuscleId,
)
