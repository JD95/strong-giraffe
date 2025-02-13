package org.wspcgir.strong_giraffe.model

data class Weight(val value: Float) {
    override fun toString(): String {
        return value.toString()
    }
}