package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Intensity.Serializer::class)
sealed class Intensity {

    object Serializer : KSerializer<Intensity> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("org.wspc.strong_giraffe.model.Intensity", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Intensity {
            val result = fromInt(Int.serializer().deserialize(decoder))
            if (result != null) {
                return result
            } else {
                throw SerializationException("Value was not an Int in the range for Intensity")
            }
        }

        override fun serialize(encoder: Encoder, value: Intensity) {
            Int.serializer().serialize(encoder, toInt(value))
        }

    }

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