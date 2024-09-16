package com.example.stronggiraffe.model

import com.example.stronggiraffe.model.ids.EquipmentId
import com.example.stronggiraffe.model.ids.LocationId

data class Equipment(val id: EquipmentId, val name: String, val location: LocationId)
