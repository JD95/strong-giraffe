package org.wspcgir.strong_giraffe.model.ids

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer


@NavTypeSerializer
class EquipmentIdSerializer : DestinationsNavTypeSerializer<EquipmentId> {
    override fun fromRouteString(routeStr: String): EquipmentId {
        return EquipmentId(routeStr)
    }

    override fun toRouteString(value: EquipmentId): String {
        return value.value
    }
}

data class EquipmentId(val value: String)