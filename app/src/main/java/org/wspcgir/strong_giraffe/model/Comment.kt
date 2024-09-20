package org.wspcgir.strong_giraffe.model

data class Comment(val value: String) {
    override fun toString(): String {
        return value
    }
}