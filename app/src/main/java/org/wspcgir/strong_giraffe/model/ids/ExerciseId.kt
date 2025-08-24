package org.wspcgir.strong_giraffe.model.ids

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ExerciseId(val value: String) : Parcelable
