package org.wspcgir.strong_giraffe.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = arrayOf("id")),
        Index(value = arrayOf("exercise")),
    ],
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exercise"),
            onDelete = CASCADE
        )
    ],
    tableName = "exercise_variation"
)
data class ExerciseVariation (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "exercise") val exercise: String,
    @ColumnInfo(name = "location") val location: String?,
)