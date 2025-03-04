package org.wspcgir.strong_giraffe.repository.entity

import androidx.room.*

@Entity(
    tableName = "workout_set",
    indices = [
        Index(value = arrayOf("id")),
        Index(value = arrayOf("exercise")),
        Index(value = arrayOf("time")),
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
        ForeignKey(
            entity = ExerciseVariation::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("variation"),
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class WorkoutSet(
    @PrimaryKey val id: String,
    @ColumnInfo(name="exercise") val exercise: String,
    @ColumnInfo(name="location") val location: String?,
    @ColumnInfo(name="equipment") val equipment: String?,
    @ColumnInfo(name="variation") val variation: String?,
    @ColumnInfo(name="reps") val reps: Int,
    @ColumnInfo(name="weight") val weight: Float,
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
