package org.wspcgir.strong_giraffe.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_variation",
    indices = [
        Index(value = arrayOf("id")),
        Index(value = arrayOf("exercise")),
        Index(value = arrayOf("location"))
    ],
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exercise"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Location::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("location"),
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class ExerciseVariation(
    @PrimaryKey val id: String,
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="exercise") val exercise: String,
    @ColumnInfo(name="location") val location: String?,
)
