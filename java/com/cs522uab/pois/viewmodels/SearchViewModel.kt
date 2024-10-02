package com.cs522uab.pois.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cs522uab.pois.api.RetrofitInstance
import com.cs522uab.pois.data.PlaceEntity
import com.cs522uab.pois.data.PlaceRepository
import com.cs522uab.pois.models.Geometry
import com.cs522uab.pois.models.Location
import com.cs522uab.pois.models.Place
import com.cs522uab.pois.models.Places
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *
 * Responsible for retrieving nearby places from the Google places API, merging favorite
 * places from the POIs database and creating favorite places within the POIs database.
 *
 *
 * @param repository The repository used to retrieve and create favorites in the POIs database.
 *
 * @property places the state object used to deliver nearby places to the Search view's UI.
 * @property error the state object used to deliver any error that occurs during the view model's processes to the Search view's UI.
 *
 */
class SearchViewModel(
    private val repository: PlaceRepository,
    private val onFavoriteChanged: () -> Unit
) : ViewModel() {

    // Holds the list of places we get from the API.
    val places: MutableState<List<Place>> = mutableStateOf(emptyList())
    val error: MutableState<String> = mutableStateOf("")

    // Tracks the list of favorite places.
    private val _favoritePlaces = MutableStateFlow<List<PlaceEntity>>(emptyList())
    val favoritePlaces: StateFlow<List<PlaceEntity>> = _favoritePlaces

    init {
        // Load the favorite places when the ViewModel is created.
        viewModelScope.launch {
            repository.allFavoritePlaces.collect { places ->
                _favoritePlaces.value = places
            }
        }
    }

    // Fetches nearby places from the API using location and radius.
    fun fetchNearbyPlaces(
        location: Location?,
        radius: Int,
        apiKey: String,
        onSuccess: (() -> Unit)? = null,
        onFailure: (() -> Unit)? = null
    ) {
        if (location == null) {
            error.value = "Location is null"
            onFailure?.invoke()
            return
        }

        places.value = emptyList()
        error.value = ""
        val locationString = "${location.lat},${location.lng}"

        // Make API call to get nearby places.
        RetrofitInstance.api.getNearbyPlaces(locationString, radius, apiKey)
            .enqueue(object : Callback<Places> {
                override fun onResponse(call: Call<Places>, response: Response<Places>) {
                    if (response.isSuccessful) {
                        val placesList = response.body()?.results ?: emptyList()
                        Log.d("SearchViewModel", "API Response: $placesList")

                        // Update the places list with data from the API.
                        places.value = placesList.map { place ->
                            val photoReference = place.photos?.firstOrNull()?.photoReference ?: ""
                            val photoUrl = getPhotoUrl(photoReference, apiKey)

                            Place(
                                name = place.name ?: "Unknown",
                                vicinity = place.vicinity ?: "Unknown",
                                location = place.location ?: location,
                                geometry = place.geometry ?: Geometry(location),
                                imageUrl = place.imageUrl ?: "",
                                photos = place.photos ?: emptyList(),
                                photoUrl = photoUrl
                            )
                        }
                        if (places.value.isEmpty()) {
                            error.value = "No places found"
                        }
                        onSuccess?.invoke()
                    } else {
                        Log.e("SearchViewModel", "API Error: ${response.message()}")
                        error.value = "Failed to retrieve places: ${response.message()}"
                        onFailure?.invoke()
                    }
                }

                override fun onFailure(call: Call<Places>, t: Throwable) {
                    Log.e("SearchViewModel", "Error fetching places: ${t.message}", t)
                    error.value = "Error: ${t.message}"
                    onFailure?.invoke()
                }
            })
    }

    // Toggle the favorite status of a place.
    fun toggleFavorite(place: Place) {
        viewModelScope.launch {
            val placeEntity = PlaceEntity(
                id = place.name ?: "",
                name = place.name ?: "",
                vicinity = place.vicinity ?: "",
                latitude = place.geometry?.location?.lat ?: 0.0,
                longitude = place.geometry?.location?.lng ?: 0.0,
                photoUrl = place.photoUrl
            )

            val existingPlace = repository.getFavoritePlaceById(placeEntity.id)
            if (existingPlace == null) {
                repository.insertFavoritePlace(placeEntity)
            } else {
                repository.deleteFavoritePlace(placeEntity)
            }
            onFavoriteChanged() // Notify about the change in favorites.
        }
    }

    // Check if a place is a favorite.
    suspend fun isFavorite(placeId: String): Boolean {
        return repository.getFavoritePlaceById(placeId) != null
    }

    // Get a photo URL using the Google Places API.
    fun getPhotoUrl(photoReference: String, apiKey: String): String {
        val url = "https://maps.googleapis.com/maps/api/place/photo?photoreference=$photoReference&key=$apiKey&maxwidth=400"
        Log.d("SearchViewModel", "Generated Photo URL: $url")
        return url
    }

    // Refresh the list of favorite places from the database.
    fun refreshFavoritePlaces() {
        viewModelScope.launch {
            _favoritePlaces.value = repository.getAllFavoritePlaces()
        }
    }
}

// Factory for creating instances of SearchViewModel.
class SearchViewModelFactory(
    private val repository: PlaceRepository,
    private val onFavoriteChanged: () -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository, onFavoriteChanged) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
