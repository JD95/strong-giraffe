package com.example.stronggiraffe.model

sealed class Intensity {
    /**
     * No Muscle activation, too light.
     */
    object NoActivation: Intensity()

    /**
     * Muscle activation, but no failure
     * at the end of the set.
     */
    object Easy: Intensity()

    /**
     * A good, productive set, failure
     * or near failure at the end.
     */
    object Normal: Intensity()

    /**
     * A hard set, failed early or
     * in the middle of the desired rep
     * range.
     */
    object EarlyFailure: Intensity()

    /**
     * Pain at any point during the set
     * caused set to be cut short.
     */
    object Pain: Intensity()

    override fun toString(): String {
        return when (this) {
            is NoActivation -> "No Activation"
            is Easy -> "Easy"
            is Normal-> "Normal"
            is EarlyFailure -> "Early Failure"
            is Pain -> "Painful"
        }
    }
}