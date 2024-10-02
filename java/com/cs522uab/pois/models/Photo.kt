package com.cs522uab.pois.models

import com.google.gson.annotations.SerializedName

// https://developers.google.com/maps/documentation/places/web-service/search-nearby
// A photo model containing the photo reference string of a place returned by Google Maps Nearby Places Search API
// This string is used to build the place's Google photos URL to display the image
data class Photo(
    @SerializedName("photo_reference") val photoReference: String,
)