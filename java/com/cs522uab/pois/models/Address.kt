package com.cs522uab.pois.models

// The model used to hold the address retrieved from the reverse geocoding service
data class Address( val street: String,
                    val cityStateZip: String)