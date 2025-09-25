package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.ids.LocationId

@Serializable
data class Location(val id: LocationId, val name: String)
