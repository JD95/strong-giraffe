package org.wspcgir.strong_giraffe.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.wspcgir.strong_giraffe.repository.entity.*
import java.util.UUID

@Database(
    entities = [
        WorkoutSet::class, Location::class,
        Exercise::class, Muscle::class,
        Equipment::class, ExerciseVariation::class
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
        database.execSQL("DROP INDEX index_workout_set_id;");
        database.execSQL("DROP INDEX index_workout_set_location_exercise_equipment;")
        database.execSQL("DROP INDEX index_workout_set_time;")

        database.execSQL(
            """
            CREATE TABLE workout_set_new(
              id TEXT NOT NULL,
              exercise TEXT NOT NULL,
              location TEXT NULL,
              equipment TEXT NULL,
              reps INTEGER NOT NULL,
              weight REAL NOT NULL,
              time INTEGER NOT NULL,
              intensity INTEGER NOT NULL,
              comment TEXT NOT NULL,
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

        database.execSQL(
            """
            CREATE TABLE exercise_variation(
              id TEXT NOT NULL,
              name TEXT NOT NULL,
              exercise TEXT NOT NULL,
              location TEXT NULL,
              PRIMARY KEY(id),
              FOREIGN KEY(exercise) REFERENCES Exercise(id) ON DELETE CASCADE,
              FOREIGN KEY(location) REFERENCES Location(id) ON DELETE CASCADE 
            );
            """.trimIndent()
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS index_exercise_variation_id ON exercise_variation(id);")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_exercise_variation_exercise ON exercise_variation(exercise);")

        var exercises : Map<String, Set<(Pair<String, String>)>> = emptyMap()
        val cursor = database.query(
            """
            SELECT
              ws.exercise AS exercise, 
              ws.location AS location, 
              e.name AS name
            FROM workout_set ws
              JOIN Equipment e ON ws.equipment = e.id
            """.trimIndent())
        while (cursor.moveToNext()){
            val exerciseId = cursor.getString(0)
            val locationId = cursor.getString(1)
            val equipmentName = cursor.getString(2)
            val variants = exercises[exerciseId] ?: emptySet()
            exercises = exercises.plus(exerciseId to variants.plus(Pair(equipmentName, locationId)))
        }
        for (pair in exercises) {
            val exerciseId = pair.key
            for (variation in pair.value) {
                val id = UUID.randomUUID()
                val name = variation.first
                val location = variation.second
                database.execSQL(
                    """ 
                    INSERT INTO exercise_variation 
                    VALUES ('$id', '$name', '$exerciseId', '$location')
                    """.trimIndent()
                )
            }
        }
    }
}
