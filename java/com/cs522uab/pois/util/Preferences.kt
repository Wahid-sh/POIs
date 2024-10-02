package com.cs522uab.pois.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 *
 * A convenient utility class to store and retrieve this app's user preferences
 *
 * @property searchRadius the configurable search radius in meters.
 * @property searchLongitude the configurable search longitude in meters.
 * @property searchLatitude the configurable search latitude in meters.
 *
 */
class Preferences(private val context: Context) {
    companion object {
        const val PREFS_NAME = "pois_prefs"
        const val SEARCH_RADIUS_KEY = "search_radius"
        const val SEARCH_LONGITUDE_KEY = "search_longitude"
        const val SEARCH_LATITUDE_KEY = "search_latitude"
    }
    private var prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    var searchRadius: Int
        get() = prefs.getInt(SEARCH_RADIUS_KEY, 0)
        set(value) = prefs.edit().putInt(SEARCH_RADIUS_KEY, value).apply()
    var searchLongitude: Double?
        get() = prefs.getString(SEARCH_LONGITUDE_KEY, "0.0")?.toDouble() ?: 0.0
        set(value) = prefs.edit().putString(SEARCH_LONGITUDE_KEY, value.toString()).apply()
    var searchLatitude: Double?
        get() = prefs.getString(SEARCH_LATITUDE_KEY, "0.0")?.toDouble() ?: 0.0
        set(value) = prefs.edit().putString(SEARCH_LATITUDE_KEY, value.toString()).apply()
}