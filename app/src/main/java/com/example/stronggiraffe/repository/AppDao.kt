package com.example.stronggiraffe.repository

import androidx.room.Insert
import androidx.room.Query
import com.example.stronggiraffe.repository.entity.Equipment
import com.example.stronggiraffe.repository.entity.Location
import com.example.stronggiraffe.repository.entity.Muscle
import com.example.stronggiraffe.repository.entity.WorkoutSet

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
                 , reps
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
}