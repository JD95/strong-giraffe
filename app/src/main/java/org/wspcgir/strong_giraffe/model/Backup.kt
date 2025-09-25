package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.Serializable


@Serializable
data class Backup(
    val locations: List<Location>,
    val muscles: List<Muscle>,
    val exercises: List<Exercise>,
    val variations: List<ExerciseVariation>,
    val sets: List<WorkoutSet>,
)
