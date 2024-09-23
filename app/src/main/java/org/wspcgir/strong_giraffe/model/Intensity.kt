package org.wspcgir.strong_giraffe.model

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

    companion object {

        fun toInt(value: Intensity): Int {
            return when (value) {
                is NoActivation -> NO_ACTIVATION
                is Easy -> EASY
                is Normal-> NORMAL
                is EarlyFailure -> EARLY_FAILURE
                is Pain -> PAIN
            }
        }

        fun fromInt(n: Int): Intensity? {
            return when (n) {
                NO_ACTIVATION -> NoActivation
                EASY -> Easy
                NORMAL -> Normal
                EARLY_FAILURE -> EarlyFailure
                PAIN -> Pain
                else -> {
                    null
                }
            }
        }

        val range: List<Intensity> = listOf(NoActivation, Easy, Normal, EarlyFailure, Pain)

        const val NO_ACTIVATION: Int = 0
        const val EASY: Int = 1
        const val NORMAL: Int = 2
        const val EARLY_FAILURE: Int = 3
        const val PAIN: Int = 4
    }
}