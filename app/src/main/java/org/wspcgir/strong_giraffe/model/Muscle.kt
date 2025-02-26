package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.MuscleId

data class Muscle(val id: MuscleId, val name: String)

val previewMuscles = listOf(
    Muscle(MuscleId("muscle01"), "bicep"),
    Muscle(MuscleId("muscle02"), "tricep"),
    Muscle(MuscleId("muscle03"), "chest"),
    Muscle(MuscleId("muscle04"), "quads"),
    Muscle(MuscleId("muscle05"), "hamstrings"),
    Muscle(MuscleId("muscle06"), "side delts"),
    Muscle(MuscleId("muscle07"), "lats"),
)
