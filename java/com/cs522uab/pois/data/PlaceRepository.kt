package com.cs522uab.pois.data

import kotlinx.coroutines.flow.Flow


class PlaceRepository(private val placeDao: PlaceDao) {

    // Live stream of all favorite places.
    val allFavoritePlaces: Flow<List<PlaceEntity>> = placeDao.getAllFavoritePlaces()

    // Adds a place to favorites.
    suspend fun insertFavoritePlace(place: PlaceEntity) {
        placeDao.insertFavoritePlace(place)
    }

    // Removes a place from favorites.
    suspend fun deleteFavoritePlace(place: PlaceEntity) {
        placeDao.deleteFavoritePlace(place)
    }

    // Finds a favorite place by ID.
    suspend fun getFavoritePlaceById(id: String): PlaceEntity? {
        return placeDao.getFavoritePlaceById(id)
    }

    // Gets a list of all favorite places.
    suspend fun getAllFavoritePlaces(): List<PlaceEntity> {
        return placeDao.getAllFavoritePlacesAsList()
    }
}
