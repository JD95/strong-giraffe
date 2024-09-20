package org.wspcgir.strong_giraffe.model.ids

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer

@NavTypeSerializer
class LocationIdSerializer : DestinationsNavTypeSerializer<LocationId> {
    override fun fromRouteString(routeStr: String): LocationId {
       return LocationId(routeStr)
    }

    override fun toRouteString(value: LocationId): String {
        return value.value
    }
}

data class LocationId(val value: String)