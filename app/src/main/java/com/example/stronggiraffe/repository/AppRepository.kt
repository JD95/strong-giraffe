package com.example.stronggiraffe.repository

import com.example.stronggiraffe.model.*
import com.example.stronggiraffe.model.ids.*
import java.time.Instant
import com.example.stronggiraffe.repository.entity.Location as LocationEntity
import com.example.stronggiraffe.repository.entity.Equipment as EquipmentEntity
import com.example.stronggiraffe.repository.entity.Muscle as MuscleEntity
import com.example.stronggiraffe.repository.entity.Exercise as ExerciseEntity
import com.example.stronggiraffe.repository.entity.WorkoutSet as WorkoutSetEntity
import java.util.UUID

class AppRepository(private val dao: AppDao) {
    suspend fun newLocation(): Location {
        val id = UUID.randomUUID().toString()
        val name = "New Location"
        dao.insertLocation(LocationEntity(id, name))
        return Location(LocationId(id), name)
    }
    suspend fun updateLocation(id: LocationId, newName: String) {
        dao.updateLocation(id.value, newName)
    }
    suspend fun getLocations(): List<Location> {
        val entities = dao.getLocations()
        return entities.map { e -> Location(LocationId(e.id), e.name) }
    }

    suspend fun getEquipment(): List<Equipment> {
        val entities = dao.getEquipment()
        return entities.map { e -> Equipment(EquipmentId(e.id), e.name, LocationId(e.location)) }
    }

    suspend fun newEquipment(location: LocationId): Equipment {
        val id = UUID.randomUUID().toString()
        val name = "New Equipment"
        dao.insertEquipment(EquipmentEntity(id, name, location.value))
        return Equipment(EquipmentId(id), name, location)
    }

    suspend fun updateEquipment(id: EquipmentId, name: String, location: LocationId) {
        dao.updateEquipment(id.value, name, location.value)
    }

    suspend fun getMuscles(): List<Muscle> {
        return dao.getAllMuscles().map { e -> Muscle(MuscleId(e.id), e.name) }
    }

    suspend fun newMuscle(): Muscle {
        val id = UUID.randomUUID().toString()
        val name = "New Muscle"
        dao.insertMuscle(MuscleEntity(id, name))
        return Muscle(MuscleId(id), name)
    }

    suspend fun updateMuscle(muscleId: MuscleId, name: String) {
        dao.updateMuscle(muscleId.value, name)
    }

    suspend fun getExercises(): List<Exercise> {
        val entities = dao.getExercises()
        return entities.map { e -> Exercise(ExerciseId(e.id), e.name, MuscleId(e.muscle)) }
    }

    suspend fun newExercise(muscle: MuscleId): Exercise {
        val id = UUID.randomUUID().toString()
        val name = "New Exercise"
        dao.insertExercise(ExerciseEntity(id, name, muscle.value))
        return Exercise(ExerciseId(id), name, muscle)

    }

    suspend fun updateExercise(id: ExerciseId, name: String, muscle: Muscle) {
        dao.updateExercise(id.value, name, muscle.id.value)
    }

    suspend fun newWorkoutSet(
        location: LocationId,
        equipment: EquipmentId,
        exercise: ExerciseId
    ): WorkoutSet {
        val id = UUID.randomUUID().toString()
        val reps = 10
        val time = Instant.now()
        val comment = ""
        val weight = 0
        dao.insertWorkoutSet(
            WorkoutSetEntity(
                id = id,
                exercise = exercise.value,
                location = location.value,
                equipment = equipment.value,
                reps = reps,
                weight = weight,
                time = time.epochSecond,
                intensity = Intensity.NORMAL,
                comment = comment
            )
        )
        return WorkoutSet(
            id = SetId(id),
            exercise = exercise,
            location = location,
            equipment = equipment,
            reps = Reps(reps),
            weight = Weight(weight),
            time = time,
            intensity = Intensity.Normal,
            comment = Comment(comment),
        )
    }

    suspend fun getSetSummaries(): List<SetSummary> {
        val entities = dao.getSetSummaries()
        return entities.map { e ->
            SetSummary(
                id = SetId(e.id),
                exerciseName = e.exerciseName,
                exerciseId = ExerciseId(e.exerciseId),
                reps = Reps(e.reps),
                weight = Weight(e.weight),
                time = Instant.ofEpochSecond(e.time),
                intensity = Intensity.fromInt(e.intensity)!!
            )
        }
    }

    suspend fun updateWorkoutSet(
        id: SetId,
        exercise: ExerciseId,
        location: LocationId,
        equipment: EquipmentId,
        reps: Reps,
        weight: Weight,
        time: Instant,
        intensity: Intensity,
        comment: Comment
    ) {
        dao.updateWorkoutSet(
            id = id.value,
            exercise = exercise.value,
            location = location.value,
            equipment = equipment.value,
            reps = reps.value,
            weight = weight.value,
            time = time.epochSecond,
            intensity = Intensity.toInt(intensity),
            comment = comment.value,
        )
    }

    suspend fun getSetFromId(id: SetId): WorkoutSet {
        val e = dao.getWorkoutSet(id.value)
        return WorkoutSet(
            id = SetId(e.id),
            exercise = ExerciseId(e.exercise),
            location = LocationId(e.location),
            equipment = EquipmentId(e.equipment),
            reps = Reps(e.reps),
            weight = Weight(e.weight),
            intensity = Intensity.fromInt(e.intensity)!!,
            time = Instant.ofEpochSecond(e.time),
            comment = Comment(e.comment)
        )
    }
}