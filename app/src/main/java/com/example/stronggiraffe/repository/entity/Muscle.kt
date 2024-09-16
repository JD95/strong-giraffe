package com.example.stronggiraffe.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["id"]),
        Index(value = ["name"]),
    ]
)
data class Muscle(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
)


