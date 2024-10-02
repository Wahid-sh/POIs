package com.cs522uab.pois.models

// https://developers.google.com/maps/documentation/places/web-service/search-nearby
// A model containing the latitude and longitude for a place returned from the Google Maps Nearby Places Search API
// This model is also used to hold the coordinates returned from the location services client
data class Location(
    val lat: Double?,
    val lng: Double?
)