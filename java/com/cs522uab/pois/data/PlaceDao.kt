package com.cs522uab.pois.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaceDao {

    // Get all favorites as a live stream of updates.
    @Query("SELECT * FROM favorite_places")
    fun getAllFavoritePlaces(): Flow<List<PlaceEntity>>

    // Get all favorites in one go.
    @Query("SELECT * FROM favorite_places")
    suspend fun getAllFavoritePlacesAsList(): List<PlaceEntity>

    // Add or replace a favorite place.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePlace(place: PlaceEntity)

    // Remove a favorite place.
    @Delete
    suspend fun deleteFavoritePlace(place: PlaceEntity)

    // Find a favorite place by its ID.
    @Query("SELECT * FROM favorite_places WHERE id = :id")
    suspend fun getFavoritePlaceById(id: String): PlaceEntity?
}
