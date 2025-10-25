package org.wspcgir.strong_giraffe.model.set

import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.Comment
import org.wspcgir.strong_giraffe.model.Intensity
import org.wspcgir.strong_giraffe.model.Reps
import org.wspcgir.strong_giraffe.model.Time
import org.wspcgir.strong_giraffe.model.Weight
import org.wspcgir.strong_giraffe.model.ids.EquipmentId
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.model.ids.SetId

@Serializable
data class WorkoutSet(
    val id: SetId,
    val exercise: ExerciseId,
    val location: LocationId?,
    val equipment: EquipmentId?,
    val variation: ExerciseVariationId?,
    val reps: Reps,
    val weight: Weight,
    val time: Time,
    val intensity: Intensity,
    val comment: Comment,
)
