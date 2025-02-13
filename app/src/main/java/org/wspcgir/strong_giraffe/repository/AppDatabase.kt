package org.wspcgir.strong_giraffe.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.wspcgir.strong_giraffe.repository.entity.*

@Database(
    entities = [
        WorkoutSet::class, Location::class,
        Exercise::class, Muscle::class,
        Equipment::class,
    ],
    views = [
        SetSummary::class
    ],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(database : SupportSQLiteDatabase) {
        database.execSQL(
            """
            DROP INDEX index_workout_set_id;
            DROP INDEX index_workout_set_location_exercise_equipment;
            DROP INDEX index_workout_set_time;
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE TABLE workout_set_new(
              intensity INTEGER NOT NULL,
              reps INTEGER NOT NULL,
              exercise TEXT NOT NULL,
              equipment TEXT NULL,
              weight REAL NOT NULL,
              location TEXT NULL,
              comment TEXT NOT NULL,
              id TEXT NOT NULL,
              time INTEGER NOT NULL,
              PRIMARY KEY(id),
              FOREIGN KEY(exercise) REFERENCES Exercise(id) ON DELETE CASCADE,
              FOREIGN KEY(location) REFERENCES Location(id) ON DELETE CASCADE 
            );
            """.trimIndent()
        )
        database.execSQL( "INSERT INTO workout_set_new SELECT * FROM workout_set;")
        database.execSQL("ALTER TABLE workout_set RENAME TO workout_set_old;")
        database.execSQL("ALTER TABLE workout_set_new RENAME TO workout_set;")
        database.execSQL("DROP TABLE workout_set_old;")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_set_id ON workout_set(id);")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_set_exercise ON workout_set(exercise);")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_set_time ON workout_set(time);")
    }
}
