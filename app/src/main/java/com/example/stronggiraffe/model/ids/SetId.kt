package com.example.stronggiraffe.model.ids

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer

@NavTypeSerializer
class SetIdSerializer : DestinationsNavTypeSerializer<SetId> {
    override fun fromRouteString(routeStr: String): SetId {
        return SetId(routeStr)
    }

    override fun toRouteString(value: SetId): String {
        return value.value
    }
}

data class SetId(val value: String)
