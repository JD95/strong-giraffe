package org.wspcgir.strong_giraffe.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = arrayOf("id")),
        Index(value = arrayOf("name")),
        Index(value = arrayOf("muscle")),
    ]
)
data class Exercise (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "muscle") val muscle: String,
)

