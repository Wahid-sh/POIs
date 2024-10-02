package com.cs522uab.pois.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cs522uab.pois.util.Preferences

/**
 *
 * Responsible for retrieving and setting the search radius user preference.
 *
 */
class SettingsViewModel: ViewModel() {
    private lateinit var prefs: Preferences

    // Function to get the search radius from the user preference and return it to the Settings view
    fun getSearchRadius(context: Context): Int {
        if(!::prefs.isInitialized){
            prefs = Preferences(context)
        }
        return(prefs.searchRadius)
    }

    // Function to set the search radius user preference to the value passed by the Settings view
    fun setSearchRadius(context: Context, radius: Int) {
        if(!::prefs.isInitialized){
            prefs = Preferences(context)
        }
        prefs.searchRadius = radius
    }
}