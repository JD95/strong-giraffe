package org.wspcgir.strong_giraffe.model

import org.wspcgir.strong_giraffe.model.ids.LocationId

data class Location(val id: LocationId, val name: String)

val previewLocations = listOf(
    Location(LocationId("locA"), "Gold's Gym"),
    Location(LocationId("locB"), "City Sports"),
    Location(LocationId("locC"), "Planet Fitness"),
    Location(LocationId("locD"), "24 Hour Fitness"),
)