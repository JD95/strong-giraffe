package org.wspcgir.strong_giraffe.repository.entity.variation

import androidx.room.ColumnInfo

data class VariationContent(
    @ColumnInfo(name="id") val id: String,
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="location") val location: String?,
    @ColumnInfo(name="locationName") val locationName: String?,
)