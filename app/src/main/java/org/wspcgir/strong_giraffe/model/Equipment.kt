package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.EquipmentId
import org.wspcgir.strong_giraffe.model.ids.LocationId

data class Equipment(val id: EquipmentId, val name: String, val location: LocationId)
