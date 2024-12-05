package org.wspcgir.strong_giraffe.repository
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Archives
        database.execSQL(
            """ 
                ALTER TABLE workout_set
                  ADD COLUMN exercise_variation TEXT NOT NULL; 
            """.trimIndent())

    }
}

class Migrations {
}