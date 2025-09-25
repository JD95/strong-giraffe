package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.Serializable

@Serializable
data class Reps(val value: Int) {
    override fun toString(): String {
        return value.toString()
    }
}