package com.example.stronggiraffe.repository

import com.example.stronggiraffe.model.Equipment
import com.example.stronggiraffe.model.Location
import com.example.stronggiraffe.model.ids.EquipmentId
import com.example.stronggiraffe.repository.entity.Location as LocationEntity
import com.example.stronggiraffe.repository.entity.Equipment as EquipmentEntity
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

    suspend fun getEquipment(): List<Equipment> {
        val entities = dao.getEquipment()
        return entities.map { e -> Equipment(EquipmentId(e.id), e.name, LocationId(e.location)) }
    }

    suspend fun newEquipment(name: String, location: LocationId): Equipment {
        val id = UUID.randomUUID().toString()
        dao.insertEquipment(EquipmentEntity(id, name, location.value))
        return Equipment(EquipmentId(id), name, location)
    }

    suspend fun updateEquipment(id: EquipmentId, name: String, location: LocationId) {
        dao.updateEquipment(id.value, name, location.value)
    }
}