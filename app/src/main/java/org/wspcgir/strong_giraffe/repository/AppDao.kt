package org.wspcgir.strong_giraffe.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.wspcgir.strong_giraffe.repository.entity.*

@androidx.room.Dao
interface AppDao {

    @Insert
    suspend fun insertLocation(value: Location)

    @Query(
        """
            UPDATE location
            SET name = :newName
            WHERE id = :id
        """
    )
    suspend fun updateLocation(id: String, newName: String)

    @Query(
        """
            SELECT id, name
            FROM location
        """
    )
    suspend fun getLocations(): List<Location>


    @Query(
        """
            select id, name
            from muscle
        """
    )
    suspend fun getAllMuscles(): List<Muscle>

    @Query(
        """
            SELECT id
                 , exercise
                 , location
                 , equipment
                 , reps
                 , weight
                 , time
                 , intensity
                 , comment
            FROM workout_set
            WHERE exercise = :exerciseId
            ORDER BY time DESC 
            LIMIT :count
        """
    )
    suspend fun getLatestNSetsForExercise(exerciseId:String, count: Int): List<WorkoutSet>

    @Query(
        """
            SELECT id
                 , name
                 , location
            FROM equipment
        """
    )
    suspend fun getEquipment(): List<Equipment>

    @Insert
    suspend fun insertEquipment(equipmentEntity: Equipment)

    @Query(
        """
            UPDATE equipment
            SET name = :name
              , location = :location
            WHERE id = :id
        """
    )
    suspend fun updateEquipment(id: String, name: String, location: String)

    @Insert
    suspend fun insertMuscle(value: Muscle)

    @Query(
        """
            UPDATE muscle
            SET name = :name
            WHERE id = :id
            
        """
    )
    suspend fun updateMuscle(id: String, name: String)

    @Query(
        """
            SELECT id
                 , name
                 , muscle
            FROM exercise
        """
    )
    suspend fun getExercises(): List<Exercise>

    @Insert
    suspend fun insertExercise(value: Exercise)

    @Query(
        """
            UPDATE exercise
            SET name = :name
              , muscle = :muscle
            WHERE id = :id
        """
    )
    suspend fun updateExercise(id: String, name: String, muscle: String)

    @Insert
    suspend fun insertWorkoutSet(workoutSetEntity: WorkoutSet)

    @Query(
        """
            SELECT id
                 , exercise
                 , location
                 , equipment
                 , reps
                 , weight
                 , time
                 , intensity
                 , comment
            FROM workout_set
        """
    )
    suspend fun getWorkoutSets(): List<WorkoutSet>

    @Query("SELECT * FROM SetSummary")
    suspend fun getSetSummaries(): List<SetSummary>

    @Query(
        """
            UPDATE workout_set
            SET exercise = :exercise
              , location = :location
              , equipment = :equipment
              , reps = :reps
              , weight = :weight
              , time = :time
              , intensity = :intensity
              , comment = :comment
            WHERE id = :id
        """
    )
    suspend fun updateWorkoutSet(
        id: String,
        exercise: String,
        location: String,
        equipment: String,
        reps: Int,
        weight: Int,
        time: Long,
        intensity: Int,
        comment: String
    )

    @Query(
        """
            SELECT id
                 , exercise
                 , location
                 , equipment
                 , reps
                 , weight
                 , time
                 , intensity
                 , comment
            FROM workout_set
            WHERE id = :id
        """
    )
    suspend fun getWorkoutSet(id: String): WorkoutSet

    @Query(
        """ 
            DELETE from location
            WHERE id = :id
        """
    )
    suspend fun deleteLocation(id: String)

    @Query(
        """ 
            DELETE from equipment
            WHERE id = :id
        """
    )
    suspend fun deleteEquipment(id: String)

    @Query(
        """ 
            DELETE from muscle 
            WHERE id = :id
        """
    )
    suspend fun deleteMuscle(id: String)

    @Query(
        """ 
            DELETE from exercise 
            WHERE id = :id
        """
    )
    suspend fun deleteExercise(id: String)

    @Query(
        """ 
            DELETE from workout_set 
            WHERE id = :id
        """
    )
    suspend fun deleteWorkoutSet(id: String)
}