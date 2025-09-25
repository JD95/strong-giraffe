package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(val value: String) {
    override fun toString(): String {
        return value
    }
}