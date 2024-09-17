package com.example.stronggiraffe.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stronggiraffe.repository.entity.*

@Database(
    entities = [
        WorkoutSet::class, Location::class,
        Exercise::class, Muscle::class,
        Equipment::class,
    ],
    version = 1,
    autoMigrations = [ ],
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}
