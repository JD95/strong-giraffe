package org.wspcgir.strong_giraffe.model.ids

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer


@NavTypeSerializer
class ExerciseVariationIdSerializer : DestinationsNavTypeSerializer<ExerciseVariationId> {
    override fun fromRouteString(routeStr: String): ExerciseVariationId {
        return ExerciseVariationId(routeStr)
    }

    override fun toRouteString(value: ExerciseVariationId): String {
        return value.value
    }
}

data class ExerciseVariationId(val value: String)