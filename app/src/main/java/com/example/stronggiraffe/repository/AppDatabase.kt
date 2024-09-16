package com.example.stronggiraffe.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stronggiraffe.repository.entity.Exercise
import com.example.stronggiraffe.repository.entity.Location
import com.example.stronggiraffe.repository.entity.Muscle
import com.example.stronggiraffe.repository.entity.WorkoutSet

@Database(
    entities = [
        WorkoutSet::class, Location::class,
        Exercise::class, Muscle::class,
    ],
    version = 1,
    autoMigrations = [ ],
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}
