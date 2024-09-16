package com.example.stronggiraffe.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = arrayOf("id")),
        Index(value = arrayOf("name")),
    ]
)
data class Location(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
)
