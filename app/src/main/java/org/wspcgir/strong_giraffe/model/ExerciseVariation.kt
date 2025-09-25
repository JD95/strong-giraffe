package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.model.ids.LocationId

@Serializable
data class ExerciseVariation(
    val id: ExerciseVariationId,
    val name: String,
    val exercise: ExerciseId,
    val location: LocationId?,
)
