package org.wspcgir.strong_giraffe.repository

import androidx.room.Database
import androidx.room.RoomDatabase
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
    version = 1,
    autoMigrations = [ ],
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}
