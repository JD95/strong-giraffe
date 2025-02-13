package org.wspcgir.strong_giraffe.repository

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
            ORDER BY name
        """
    )
    suspend fun getLocations(): List<Location>

    @Query(
        """
            SELECT DISTINCT l.id as id, l.name as name
            FROM location l
              JOIN equipment e ON e.location = l.id
            ORDER BY l.name
        """
    )
    suspend fun getLocationsWithEquipment(): List<Location>

    @Query(
        """
            SELECT DISTINCT m.id as id, m.name as name
            FROM muscle m
              JOIN exercise e ON e.muscle = m.id
            ORDER BY m.name
        """
    )
    suspend fun getMusclesWithExercise(): List<Muscle>


    @Query(
        """
            SELECT id, name
            FROM muscle
            ORDER BY name
        """
    )
    suspend fun getAllMuscles(): List<Muscle>

    @Query(
        """
            SELECT id
                 , name
                 , location
            FROM equipment
            ORDER BY location, name
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
            ORDER BY name
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
            LIMIT 1
        """
    )
    suspend fun getWorkoutSet(id: String): WorkoutSet

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
            ORDER BY time DESC
            LIMIT 1
        """
    )
    suspend fun getLatestWorkoutSet(): WorkoutSet?

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
            WHERE id != :id
            ORDER BY time DESC
            LIMIT 1
        """
    )
    suspend fun getLatestWorkoutSetNot(id: String): WorkoutSet?

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
            WHERE id != :set
              AND location = :location
              AND exercise = :exercise
            ORDER BY time DESC
            LIMIT 1
        """
    )
    suspend fun getLatestWorkoutSetForExerciseAtLocationExcluding(
        set: String,
        location: String,
        exercise: String
    ): WorkoutSet?

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
            WHERE time < :cutoff
              AND exercise = :exercise
              AND equipment= :equipment
            ORDER BY time DESC
            LIMIT :limit
        """
    )
    suspend fun workoutSetsForExerciseWithEquipmentBefore(
        cutoff: Long,
        exercise: String,
        equipment: String,
        limit: Int
    ): List<WorkoutSet>

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

    @Query(
        """
            SELECT m.id AS muscle_id
                 , m.name AS muscle_name
                 , SUM(IFNULL(set_counts.count, 0)) AS set_count
            FROM muscle m
              LEFT JOIN (
                SELECT e.muscle AS muscle_id
                     , 1 AS count
                FROM workout_set ws
                  JOIN exercise e ON e.id = ws.exercise
                WHERE :weekStart < ws.time 
                  AND ws.time < :weekEnd 
                  AND 1 < ws.intensity 
                  AND ws.intensity < 4
                ) AS set_counts ON set_counts.muscle_id = m.id
            GROUP BY m.id, m.name
            ORDER BY m.name
        """
    )
    suspend fun setsInWeek(weekStart: Long, weekEnd: Long): List<MuscleSetCount>

    @Query(
        """
            DELETE from location
        """
    )
    suspend fun deleteAllLocations()

    @Query(
        """
            DELETE from muscle 
        """
    )
    suspend fun deleteAllMuscles()

    @Query(
        """
            DELETE from equipment 
        """
    )
    suspend fun deleteAllEquipment()

    @Query(
        """
            DELETE from exercise 
        """
    )
    suspend fun deleteAllExercises()

    @Query(
        """
            DELETE from workout_set 
        """
    )
    suspend fun deleteAllWorkoutSets()
}