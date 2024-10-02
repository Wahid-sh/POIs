package com.cs522uab.pois.models

// https://developers.google.com/maps/documentation/places/web-service/search-nearby
// A containing model for the Location model because the Google Maps Nearby Places Search API returns the data with this structure
data class Geometry(
    val location: Location
)
