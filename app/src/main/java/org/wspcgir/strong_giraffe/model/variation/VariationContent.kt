package org.wspcgir.strong_giraffe.model.variation

import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.model.ids.LocationId

@Serializable
data class VariationContent(
    val id: ExerciseVariationId,
    val name: String,
    val location: LocationId?,
    val locationName: String?
)
