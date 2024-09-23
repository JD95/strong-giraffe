package org.wspcgir.strong_giraffe.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class MuscleSetCount(
    @ColumnInfo(name = "muscle_id") val muscleId: String,
    @ColumnInfo(name = "muscle_name") val muscleName: String,
    @ColumnInfo(name = "set_count") val setCount: Int,
)