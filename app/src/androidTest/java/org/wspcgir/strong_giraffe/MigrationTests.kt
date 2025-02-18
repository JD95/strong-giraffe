package org.wspcgir.strong_giraffe

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.wspcgir.strong_giraffe.repository.AppDatabase
import org.wspcgir.strong_giraffe.repository.MIGRATION_1_2
import java.io.IOException
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class MigrationTests {
    private val TEST_DB = "migration-test"

    private val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2
    )

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }

    private fun createRandomLocationV1(db: SupportSQLiteDatabase): String {
        val id = UUID.randomUUID().toString()
        val allowedChars = ('a'..'z')
        val nameLength = (2..10).random()
        val name = (1..nameLength).map { allowedChars.random() }.joinToString("")
        db.execSQL( "INSERT INTO Location (id, name) VALUES ('$id', '$name');")
        return id
    }
    private fun createRandomMuscleV1(db: SupportSQLiteDatabase): String {
        val id = UUID.randomUUID().toString()
        val allowedChars = ('a'..'z')
        val nameLength = (2..10).random()
        val name = (1..nameLength).map { allowedChars.random() }.joinToString("")
        db.execSQL( "INSERT INTO Muscle (id, name) VALUES('$id', '$name');")
        return id
    }
    private fun createRandomExerciseV1(db: SupportSQLiteDatabase, muscle: String): String {
        val id = UUID.randomUUID().toString()
        val allowedChars = ('a'..'z')
        val nameLength = (2..10).random()
        val name = (1..nameLength).map { allowedChars.random() }.joinToString("")
        db.execSQL( "INSERT INTO Exercise (id, name, muscle) VALUES ('$id', '$name', '$muscle');")
        return id
    }
    private fun createRandomEquipmentV1(db: SupportSQLiteDatabase, location: String): String {
        val id = UUID.randomUUID().toString()
        val allowedChars = ('a'..'z')
        val nameLength = (2..10).random()
        val name = (1..nameLength).map { allowedChars.random() }.joinToString("")
        db.execSQL( "INSERT INTO Equipment (id, name, location) VALUES ('$id', '$name', '$location');")
        return id
    }
    private fun createRandomWorkoutSetV1(
        db: SupportSQLiteDatabase,
        exercise: String,
        equipment: String,
        location: String
    ): String {
        val id = UUID.randomUUID().toString()
        val rep = (5..12).random()
        val weight = (50..100).random()
        val time = (0..1000).random()
        val intensity = (0..4).random()
        db.execSQL(
            """
            INSERT INTO workout_set (id, exercise, location, equipment, reps, weight, time, intensity, comment)
            VALUES ('$id', '$exercise', '$location', '$equipment', $rep, $weight, $time, $intensity, 'comment')
            """.trimIndent()
        )
        return id
    }

    @Test
    @Throws(IOException::class)
    fun migrate_1_2() {
        var seen: Map<String, Set<Pair<String, String>>> = emptyMap()
        helper.createDatabase(TEST_DB, 1).apply {
            val muscle = createRandomMuscleV1(this)
            val exercises = (1..10).map { createRandomExerciseV1(this, muscle) }
            val locations = (1..3).map { createRandomLocationV1(this) }
            val equipments = (1..10).map {
                val location = locations.random()
                Pair(createRandomEquipmentV1(this, location), location)
            }

            (1..20).map {
                val exercise = exercises.random()
                val pair = equipments.random()
                val equipment = pair.first
                val location = pair.second
                val variations = seen[exercise] ?: emptySet()
                seen = seen.plus(exercise to variations.plus(Pair(equipment, location)))
                createRandomWorkoutSetV1(this, exercise, equipment, location)
            }
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, false, MIGRATION_1_2)
        val cursor = db.query("SELECT * FROM exercise_variation")
        assertEquals(seen.map { it.value.size }.sum(), cursor.count)
    }
}