package com.example.stronggiraffe.repository

import com.example.stronggiraffe.model.Location
import com.example.stronggiraffe.repository.entity.Location as LocationEntity
import com.example.stronggiraffe.model.ids.LocationId
import java.util.UUID

class AppRepository(private val dao: AppDao) {
    suspend fun newLocation(): Location {
        val id = UUID.randomUUID().toString()
        val name = "New Location"
        dao.insertLocation(LocationEntity(id, name))
        return Location(LocationId(id), name)
    }
    suspend fun updateLocation(id: LocationId, newName: String) {
        dao.updateLocation(id.value, newName)
    }
    suspend fun getLocations(): List<Location> {
        val entities = dao.getLocations()
        return entities.map { e -> Location(LocationId(e.id), e.name) }
    }
}