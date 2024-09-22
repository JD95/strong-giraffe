package org.wspcgir.strong_giraffe.repository.entity

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    indices = [
        Index(value = arrayOf("id")),
        Index(value = arrayOf("muscle")),
    ],
    foreignKeys = [
        ForeignKey(
            entity = Muscle::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("muscle"),
            onDelete = CASCADE
        )
    ]

)
data class Exercise (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "muscle") val muscle: String,
)

