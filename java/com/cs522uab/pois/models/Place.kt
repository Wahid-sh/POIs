package com.cs522uab.pois.models

// https://developers.google.com/maps/documentation/places/web-service/search-nearby
// A place model containing all of the desired fields for a place returned by the Google Maps Nearby Places Search API
// To use more fields returned by the API, add the exact field and data type to this model, reference the API documentation
data class Place(
    val name: String?,
    val vicinity: String?,
    val location: Location?,
    val geometry: Geometry?,
    val imageUrl: String?,
    val photos: List<Photo>?,
    val photoUrl: String?
)