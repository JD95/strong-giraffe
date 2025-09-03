package org.wspcgir.strong_giraffe.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

@Serializable(with = Time.Serializer::class)
data class Time(val value: Instant) {

    object Serializer : KSerializer<Time> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("org.wspcgir.strong_giraffe.model.Time", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Time {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: Time) {
            TODO("Not yet implemented")
        }

    }
}