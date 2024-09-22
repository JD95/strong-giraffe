package org.wspcgir.strong_giraffe.repository.entity

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    indices = [
        Index(value = arrayOf("id")),
        Index(value = arrayOf("location")),
    ],
    foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("location"),
            onDelete = CASCADE
        )
    ]
)
data class Equipment(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "location") val location: String,
)
