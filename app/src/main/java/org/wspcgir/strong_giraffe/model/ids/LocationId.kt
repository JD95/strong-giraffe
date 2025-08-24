package org.wspcgir.strong_giraffe.model.ids

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LocationId(val value: String) : Parcelable