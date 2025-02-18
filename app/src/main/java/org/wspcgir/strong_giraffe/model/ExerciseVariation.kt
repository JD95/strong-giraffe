package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.model.ids.LocationId

data class ExerciseVariation(
    val id: ExerciseVariationId,
    val name: String,
    val exercise: ExerciseId,
    val location: LocationId?,
)
