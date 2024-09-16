package com.example.stronggiraffe.model.ids

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer


@NavTypeSerializer
class ExerciseIdSerializer : DestinationsNavTypeSerializer<ExerciseId> {
    override fun fromRouteString(routeStr: String): ExerciseId {
        return ExerciseId(routeStr)
    }

    override fun toRouteString(value: ExerciseId): String {
        return value.value
    }
}

data class ExerciseId(val value: String)
