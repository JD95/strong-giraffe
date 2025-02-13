package org.wspcgir.strong_giraffe.repository

import org.wspcgir.strong_giraffe.model.*
import org.wspcgir.strong_giraffe.model.ids.*
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*
import org.wspcgir.strong_giraffe.repository.entity.Location as LocationEntity
import org.wspcgir.strong_giraffe.repository.entity.Equipment as EquipmentEntity
import org.wspcgir.strong_giraffe.repository.entity.Muscle as MuscleEntity
import org.wspcgir.strong_giraffe.repository.entity.Exercise as ExerciseEntity
import org.wspcgir.strong_giraffe.repository.entity.WorkoutSet as WorkoutSetEntity

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
    suspend fun getLocationsWithEquipment(): List<Location> {
        val entities = dao.getLocationsWithEquipment()
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

    suspend fun getMusclesWithExerise(): List<Muscle> {
        return dao.getMusclesWithExercise().map { e -> Muscle(MuscleId(e.id), e.name) }
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
        exercise: ExerciseId,
        time: Instant = Instant.now(),
    ): WorkoutSet {
        val id = UUID.randomUUID().toString()
        val reps = 10
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
                time = OffsetDateTime.ofInstant(
                    Instant.ofEpochSecond(e.time),
                    TimeZone.getDefault().toZoneId()
                ),
                intensity = Intensity.fromInt(e.intensity)!!
            )
        }
    }

    suspend fun updateWorkoutSet(
        original: WorkoutSet,
        exercise: ExerciseId? = null,
        location: LocationId? = null,
        equipment: EquipmentId? = null,
        reps: Reps? = null,
        weight: Weight? = null,
        time: Instant? = null,
        intensity: Intensity? = null,
        comment: Comment? = null
    ) {
        updateWorkoutSet(
            id = original.id,
            exercise = exercise ?: original.exercise,
            location = location ?: original.location,
            equipment = equipment ?: original.equipment,
            reps = reps ?: original.reps,
            weight = weight ?: original.weight,
            time = time ?: original.time,
            intensity = intensity ?: original.intensity,
            comment = comment ?: original.comment,
        )
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

    suspend fun deleteLocation(id: LocationId) {
        dao.deleteLocation(id.value)
    }

    suspend fun deleteEquipment(id: EquipmentId) {
        dao.deleteEquipment(id.value)
    }

    suspend fun deleteMuscle(id: MuscleId) {
        dao.deleteMuscle(id.value)
    }

    suspend fun deleteExercise(id: ExerciseId) {
        dao.deleteExercise(id.value)
    }

    suspend fun deleteWorkoutSet(id: SetId) {
        dao.deleteWorkoutSet(id.value)
    }

    suspend fun setsForMusclesInWeek(now: Instant): SetsForMuscleInWeek {
        val zone = TimeZone.getDefault()
        val range = WeekRange.forInstant(now, zone)
        val lastWeek = dao
            .setsInWeek(
                range.start.minusWeeks(1).toEpochSecond(),
                range.end.minusWeeks(1).toEpochSecond()
            )
            .fold(emptyMap<MuscleId, Int>()) { map, x ->
                map.plus(MuscleId(x.muscleId) to x.setCount)
            }
        val thisWeek = dao
            .setsInWeek(range.start.toEpochSecond(), range.end.toEpochSecond())
            .fold(emptyMap<MuscleId, MuscleSetHistory>()) { map, x ->
                val id = MuscleId(x.muscleId)
                map.plus(
                    id to MuscleSetHistory(x.muscleName, x.setCount, lastWeek[id] ?: 0)
                )
            }
        return SetsForMuscleInWeek(range, thisWeek)
    }

    private fun workoutSetFromEntity(e: WorkoutSetEntity): WorkoutSet {
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

    suspend fun latestSet(): WorkoutSet? {
        val e = dao.getLatestWorkoutSet()
        return if (e != null) { workoutSetFromEntity(e) } else { null }
    }

    suspend fun latestSetNot(set: SetId): WorkoutSet? {
        val e = dao.getLatestWorkoutSetNot(set.value)
        return if (e != null) { workoutSetFromEntity(e) } else { null }
    }

    suspend fun latestSetForExerciseAtLocationExcluding(
        set: SetId,
        location: LocationId,
        exercise: ExerciseId
    ): WorkoutSet? {
        val e = dao.getLatestWorkoutSetForExerciseAtLocationExcluding(
            set.value,
            location.value,
            exercise.value
        )
        return if (e != null) { workoutSetFromEntity(e) } else { null }
    }

    suspend fun dropDb() {
        dao.deleteAllLocations()
        dao.deleteAllMuscles()
        dao.deleteAllEquipment()
        dao.deleteAllExercises()
        dao.deleteAllWorkoutSets()
    }

    suspend fun setForExerciseAndEquipmentBefore(
        cutoff: Instant,
        exercise: ExerciseId,
        equipment: EquipmentId,
        limit: Int
    ): List<WorkoutSet> {
        val es = dao.workoutSetsForExerciseWithEquipmentBefore(
            cutoff.epochSecond,
            exercise.value,
            equipment.value,
            limit
        )
        return es.map { e -> workoutSetFromEntity(e) }
    }
}