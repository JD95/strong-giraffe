package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.ids.EquipmentId
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.model.ids.SetId

@Serializable
data class SetContent(
    val id: SetId,
    val exercise: ExerciseId,
    val exerciseName: String,
    val variation: ExerciseVariationId?,
    val variationName: String?,
    val reps: Reps,
    val weight: Weight,
    val time: Time,
    val intensity: Intensity,
    val comment: Comment,
)
