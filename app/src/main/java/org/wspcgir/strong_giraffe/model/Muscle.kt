package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.ids.MuscleId

@Serializable
data class Muscle(val id: MuscleId, val name: String)
