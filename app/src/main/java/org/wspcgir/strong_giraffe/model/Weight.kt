package org.wspcgir.strong_giraffe.model

data class Weight(val value: Int) {
    override fun toString(): String {
        return value.toString()
    }
}