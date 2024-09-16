package com.example.stronggiraffe.model

import com.example.stronggiraffe.model.ids.SetId
import java.time.Instant

data class SetSummary(
    val id: SetId,
    val exercise: String,
    val reps: Int,
    val weight: Int,
    val time: Instant,
    val intensity: Intensity,
)
