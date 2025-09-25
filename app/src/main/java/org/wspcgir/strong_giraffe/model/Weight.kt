package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.Serializable

@Serializable
data class Weight(val value: Float) {
    override fun toString(): String {
        return value.toString()
    }
}