package org.wspcgir.strong_giraffe.repository.entity.set

data class SetContent(
    val id: String,
    val exercise: String,
    val exerciseName: String,
    val variation: String?,
    val variationName: String?,
    val reps: Int,
    val weight: Float,
    val time: Long,
    val intensity: Int,
    val comment: String,
)