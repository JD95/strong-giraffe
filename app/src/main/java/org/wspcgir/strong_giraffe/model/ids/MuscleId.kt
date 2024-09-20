package org.wspcgir.strong_giraffe.model.ids

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer

@NavTypeSerializer
class MuscleIdSerializer : DestinationsNavTypeSerializer<MuscleId> {
    override fun fromRouteString(routeStr: String): MuscleId {
        return MuscleId(routeStr)
    }

    override fun toRouteString(value: MuscleId): String {
        return value.value
    }
}

data class MuscleId(val value: String)

