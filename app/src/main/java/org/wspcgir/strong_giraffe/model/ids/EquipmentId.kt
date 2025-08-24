package org.wspcgir.strong_giraffe.model.ids

import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
@kotlinx.parcelize.Parcelize
data class EquipmentId(val value: String) : Parcelable