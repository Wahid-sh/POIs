package com.cs522uab.pois.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.cs522uab.pois.models.Places

// https://developers.google.com/maps/documentation/places/web-service/search-nearby
// Google Maps nearby places search API route and parameter definition
interface GooglePlacesApi {
    @GET("maps/api/place/nearbysearch/json")
    fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("key") apiKey: String
    ): Call<Places>
}

// Define the Retrofit Instance with the base API URL to the Google Maps API
object RetrofitInstance {
    private const val BASE_URL = "https://maps.googleapis.com/"

    val api: GooglePlacesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GooglePlacesApi::class.java)
    }
}