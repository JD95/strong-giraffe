package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.EquipmentId
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.model.ids.SetId
import java.time.Instant

data class WorkoutSet(
    val id: SetId,
    val exercise: ExerciseId,
    val location: LocationId,
    val equipment: EquipmentId,
    val reps: Reps,
    val weight: Weight,
    val time: Instant,
    val intensity: Intensity,
    val comment: Comment,
)
