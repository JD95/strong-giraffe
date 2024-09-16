package com.example.stronggiraffe.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_set",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exercise"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutSet(
    @PrimaryKey val id: String,
    @ColumnInfo(name="exercise") val exercise: String,
    @ColumnInfo(name="location") val location: String,
    @ColumnInfo(name="reps") val reps: Int,
    @ColumnInfo(name="time") val time: Long,
    /**
     * 0 - No Activation
     * 1 - Activation, No Failure
     * 2 - Activation, Failure at End
     * 3 - Early Failure
     * 4 - Early Failure, Pain
     */
    @ColumnInfo(name="intensity") val intensity: Int,
    @ColumnInfo(name="comment") val comment: String,
)
