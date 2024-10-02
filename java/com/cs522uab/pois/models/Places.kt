package com.cs522uab.pois.models

// https://developers.google.com/maps/documentation/places/web-service/search-nearby
// A places list to hold multiple places returned by the Google Maps Nearby Places Search API
data class Places(
    val results: List<Place>
)

