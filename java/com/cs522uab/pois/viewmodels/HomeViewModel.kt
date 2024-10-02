package com.cs522uab.pois.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cs522uab.pois.data.PlaceEntity
import com.cs522uab.pois.data.PlaceRepository
import com.cs522uab.pois.models.Address
import com.cs522uab.pois.models.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

/**
 *
 * Responsible for retrieving the device's current Location from the device's location service,
 * retrieving the device's current address from the reverse geocoding service,
 * and retrieves and deletes favorite places from the POIs database
 *
 * @param repository The repository used to retrieve and delete favorites from the POIs database.
 *
 * @property location the state object used to deliver the GPS coordinates of the device to the Home view's UI.
 * @property address the state object used to deliver the address of the device to the Home view's UI.
 * @property error the state object used to deliver any error that occurs during the view model's processes to the Home view's UI.
 *
 */
class HomeViewModel(private val repository: PlaceRepository) : ViewModel() {
    private lateinit var locationClient: FusedLocationProviderClient
    val location: MutableState<Location> = mutableStateOf(Location(0.0, 0.0))
    val address: MutableState<Address> = mutableStateOf(Address("", ""))
    val error: MutableState<String> = mutableStateOf("")

    private val _favoritePlaces = MutableStateFlow<List<PlaceEntity>>(emptyList())
    val favoritePlaces: StateFlow<List<PlaceEntity>> = _favoritePlaces

    init {
        refreshFavoritePlaces() // Load favorite places when ViewModel is created.
    }

    // Reload favorite places from the database.
    fun refreshFavoritePlaces() {
        viewModelScope.launch {
            try {
                _favoritePlaces.value = repository.getAllFavoritePlaces()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error refreshing favorite places: ${e.message}")
                error.value = "Failed to refresh favorite places: ${e.message}"
            }
        }
    }

    // Get the user's current location if permission is granted.
    fun getUserLocation(context: Context, onSuccess: (() -> Unit)? = null, onFailure: (() -> Unit)? = null) {
        if (!::locationClient.isInitialized) {
            locationClient = LocationServices.getFusedLocationProviderClient(context)
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token,
            ).addOnSuccessListener { fetchedLocation ->
                if (fetchedLocation != null) {
                    val retrievedLocation = Location(fetchedLocation.latitude, fetchedLocation.longitude)
                    Log.d("HomeViewModel", "Fetched location: $retrievedLocation")
                    location.value = retrievedLocation
                    getUserAddress(context, retrievedLocation, onSuccess, onFailure)
                } else {
                    error.value = "Failed to fetch location."
                    Log.e("HomeViewModel", "Failed to fetch location.")
                    onFailure?.invoke()
                }
            }.addOnFailureListener { exception ->
                error.value = "Failed to get location: ${exception.message}"
                Log.e("HomeViewModel", "Failed to get location: ${exception.message}")
                onFailure?.invoke()
            }
        }
    }

    // Use Geocoder to convert the user's location to a readable address.
    fun getUserAddress(context: Context, location: Location, onSuccess: (() -> Unit)? = null, onFailure: (() -> Unit)? = null) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = location.lat?.let { lat ->
                location.lng?.let { lng ->
                    geocoder.getFromLocation(lat, lng, 1)
                }
            }
            if (addresses != null && addresses.isNotEmpty()) {
                val addressInfo = addresses[0]
                val retrievedAddress = Address(
                    "${addressInfo?.subThoroughfare ?: ""} ${addressInfo?.thoroughfare ?: ""}",
                    "${addressInfo?.locality ?: ""}, ${addressInfo?.adminArea ?: ""}, ${addressInfo?.postalCode ?: ""}"
                )
                Log.d("HomeViewModel", "Fetched address: $retrievedAddress")
                address.value = retrievedAddress
                onSuccess?.invoke()
            } else {
                Log.e("HomeViewModel", "No address found for the location")
                error.value = "No address found for the location"
                onFailure?.invoke()
            }
        } catch (e: IOException) {
            error.value = "Geocoder service not available: ${e.message}"
            Log.e("HomeViewModel", "Geocoder service not available: ${e.message}")
            onFailure?.invoke()
        }
    }

    // Remove a place from the favorites list.
    fun unfavoritePlace(place: PlaceEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFavoritePlace(place)
                refreshFavoritePlaces()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error unfavoriting place: ${e.message}")
                error.value = "Failed to unfavorite place: ${e.message}"
            }
        }
    }
}

// Factory to create HomeViewModel with the necessary rep.
// It allows us to pass the repository dependency to the ViewModel, which is not
// directly possible using the default ViewModel constructor.
class HomeViewModelFactory(private val repository: PlaceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // Suppress the unchecked cast warning and create the HomeViewModel with the repo.
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
