package org.wspcgir.strong_giraffe

import kotlinx.coroutines.launch

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.wspcgir.strong_giraffe.model.Intensity
import org.wspcgir.strong_giraffe.repository.AppDatabase
import org.wspcgir.strong_giraffe.repository.AppRepository
import java.time.Instant

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest() {

   private var repo: AppRepository

    init {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.databaseBuilder(appContext, AppDatabase::class.java, "test.db")
            .build()
        repo = AppRepository(db.dao())
    }

    @Test
    fun setsForMusclesInWeek_empty() {
        runBlocking() {
            launch {
                repo.dropDb()
                val length = repo.setsForMusclesInWeek(Instant.now()).setCounts.size
                assertEquals(0, length)
            }
        }
    }

    @Test
    fun setsForMusclesInWeek_one_muscle_no_sets() {
        runBlocking() {
            launch {
                repo.dropDb()
                val muscle = repo.newMuscle()
                repo.newExercise(muscle.id)
                val length = repo.setsForMusclesInWeek(Instant.now()).setCounts.size
                assertEquals(1, length)
            }
        }
    }

    @Test
    fun setsForMusclesInWeek_one_muscle_one_set() {
        runBlocking() {
            launch {
                repo.dropDb()
                val location = repo.newLocation()
                val muscle = repo.newMuscle()
                val exercise = repo.newExercise(muscle.id)
                val equipment = repo.newEquipment(location.id)
                repo.newWorkoutSet(location.id, equipment.id, exercise.id)

                val setCounts = repo.setsForMusclesInWeek(Instant.now()).setCounts
                val length = repo.setsForMusclesInWeek(Instant.now()).setCounts.size
                assertEquals(1, length)
                assertEquals(1, setCounts[muscle.id]?.thisWeek)
            }
        }
    }

    @Test
    fun setsForMusclesInWeek_easy_and_painful_sets_not_counted() {
        runBlocking() {
            launch {
                repo.dropDb()
                val location = repo.newLocation()
                val muscle = repo.newMuscle()
                val exercise = repo.newExercise(muscle.id)
                val equipment = repo.newEquipment(location.id)
                for (i in Intensity.range) {
                    val set = repo.newWorkoutSet(location.id, equipment.id, exercise.id)
                    repo.updateWorkoutSet(set, intensity = i)
                }

                val setCounts = repo.setsForMusclesInWeek(Instant.now()).setCounts
                val length = repo.setsForMusclesInWeek(Instant.now()).setCounts.size
                assertEquals(1, length)
                assertEquals(2, setCounts[muscle.id]?.thisWeek)
            }
        }
    }

    @Test
    fun setsForMusclesInWeek_one_muscle_four_sets() {
        runBlocking() {
            launch {
                repo.dropDb()
                val location = repo.newLocation()
                val muscle = repo.newMuscle()
                val exercise = repo.newExercise(muscle.id)
                val equipment = repo.newEquipment(location.id)
                for (i in 1..4) {
                    repo.newWorkoutSet(location.id, equipment.id, exercise.id)
                }

                val setCounts = repo.setsForMusclesInWeek(Instant.now()).setCounts
                val length = repo.setsForMusclesInWeek(Instant.now()).setCounts.size
                assertEquals(1, length)
                assertEquals(4, setCounts[muscle.id]?.thisWeek)
            }
        }
    }

    @Test
    fun setsForMusclesInWeek_two_muscles() {
        runBlocking() {
            launch {
                repo.dropDb()

                val muscleA = repo.newMuscle()
                val exerciseA = repo.newExercise(muscleA.id)

                val muscleB = repo.newMuscle()
                repo.newExercise(muscleB.id)

                val location = repo.newLocation()
                val equipment = repo.newEquipment(location.id)
                repo.newWorkoutSet(location.id, equipment.id, exerciseA.id)

                val muscles = repo.getMuscles()

                assertEquals(2, muscles.size)

                val setCounts = repo.setsForMusclesInWeek(Instant.now()).setCounts
                val length = repo.setsForMusclesInWeek(Instant.now()).setCounts.size
                assertEquals(2, length)
                assertEquals(1, setCounts[muscleA.id]?.thisWeek)
                assertEquals(0, setCounts[muscleB.id]?.thisWeek)
            }
        }
    }
}