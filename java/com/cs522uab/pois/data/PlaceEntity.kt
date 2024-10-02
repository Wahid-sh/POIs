package com.cs522uab.pois.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// The model used to store and retrieve places from the favorite_places table in our POIs database
@Entity(tableName = "favorite_places")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val vicinity: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String?
)