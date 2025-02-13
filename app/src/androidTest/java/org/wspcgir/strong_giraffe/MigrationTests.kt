package org.wspcgir.strong_giraffe

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
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

    @Test
    @Throws(IOException::class)
    fun migrate_1_2() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL( "INSERT INTO Muscle VALUES ('muscleA', 'bicep');")
            execSQL( "INSERT INTO Exercise VALUES ('exerciseA', 'bicep curl', 'muscleA');")
            execSQL("INSERT INTO Location VALUES ('locationA', 'gym');")
            execSQL("INSERT INTO Equipment VALUES ('equipmentA', 'dumbbell', 'locationA');")
            execSQL("INSERT INTO Equipment VALUES ('equipmentB', 'cables', 'locationA');")
            execSQL(
                """
                INSERT INTO workout_set 
                  VALUES ('setA', 'exerciseA', 'locationA', 'equipmentA', 0, 0, 123, 3, '1'), 
                         ('setB', 'exerciseA', 'locationA', 'equipmentB', 1, 2, 345, 3, '2');
                         ('setC', 'exerciseA', 'locationA', 'equipmentB', 2, 1, 543, 3, '3');
                """.trimIndent())
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)
        val cursor = db.query("SELECT * FROM exercise_variation")
        assertEquals(cursor.count, 2)
    }
}