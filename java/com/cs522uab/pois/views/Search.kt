package com.cs522uab.pois.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.cs522uab.pois.R
import com.cs522uab.pois.models.Location
import com.cs522uab.pois.models.Place
import com.cs522uab.pois.sharedui.ScreenScaffold
import com.cs522uab.pois.util.Preferences
import com.cs522uab.pois.viewmodels.SearchViewModel
import kotlinx.coroutines.launch

/**
 *
 * Displays a list of nearby places with images and allows the user to
 * create favorite places in the database by ticking the favorite icon
 * of the place in the search list.
 *
 *
 * @param searchViewModel the view model that retrieves and updates the data
 */
@Composable
fun SearchScreen(searchViewModel: SearchViewModel) {
    val context = LocalContext.current
    val apiKey = "xxxx" // You can switch to using BuildConfig.GOOGLE_PLACES_API_KEY for safety.

    ScreenScaffold("Search for nearby places") { padding ->
        ConstraintLayout(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val (placesList, message, refreshButton, progressIndicator) = createRefs()
            val places by remember { searchViewModel.places }
            val scope = rememberCoroutineScope()
            val searchMessage by remember { searchViewModel.error }
            var isLoading by remember { mutableStateOf(false) }

            // Display a list of places using a lazy column.
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .constrainAs(placesList) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(refreshButton.top)
                    }
            ) {
                items(places) { place ->
                    PlaceItem(
                        place = place,
                        searchViewModel = searchViewModel,
                        onFavoriteToggle = { searchViewModel.toggleFavorite(place) }
                    )
                }
            }

            // Show any error messages related to search.
            Text(
                modifier = Modifier.constrainAs(message) {
                    top.linkTo(placesList.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(refreshButton.top)
                },
                text = searchMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )

            // Button to start a search for nearby places.
            Button(
                enabled = !isLoading,
                onClick = {
                    isLoading = true
                    val prefs = Preferences(context)
                    val latitude = prefs.searchLatitude ?: 0.0
                    val longitude = prefs.searchLongitude ?: 0.0
                    val radius = prefs.searchRadius

                    val location = Location(latitude, longitude)
                    Log.d("SearchScreen", "Created Location: $location")

                    // Launch a coroutine to fetch nearby places.
                    scope.launch {
                        searchViewModel.fetchNearbyPlaces(location, radius, apiKey, {
                            isLoading = false
                        }, {
                            isLoading = false
                        })
                    }
                },
                modifier = Modifier.constrainAs(refreshButton) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ) {
                Text("Search Nearby Places")
            }

            // Show a progress indicator when loading.
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.constrainAs(progressIndicator) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            }
        }
    }
}

@Composable
fun PlaceItem(
    place: Place,
    searchViewModel: SearchViewModel,
    onFavoriteToggle: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Check if a place is already marked as favorite.
    LaunchedEffect(place) {
        isFavorite = place.name?.let { searchViewModel.isFavorite(it) } ?: false
    }

    // Display a card for each place with its details.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                // Icon button to toggle favorite status.
                IconButton(onClick = {
                    scope.launch {
                        onFavoriteToggle()
                        isFavorite = !isFavorite
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                        contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
                Column {
                    Text(
                        text = place.name ?: "Unknown Place",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = place.vicinity ?: "No address available",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display an image of the place, if available.
            AsyncImage(
                model = place.photoUrl,
                contentDescription = "Place image",
                placeholder = painterResource(id = R.drawable.place_holder),
                error = painterResource(id = R.drawable.place_holder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}