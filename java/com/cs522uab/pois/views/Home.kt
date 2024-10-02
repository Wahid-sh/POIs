package com.cs522uab.pois.views

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.cs522uab.pois.data.PlaceEntity
import com.cs522uab.pois.models.Location
import com.cs522uab.pois.sharedui.RequestLocationPermission
import com.cs522uab.pois.sharedui.ScreenScaffold
import com.cs522uab.pois.util.Preferences
import com.cs522uab.pois.viewmodels.HomeViewModel


// Keep track of the user's location request and refresh button state.
private val requestUserLocation = mutableStateOf(false)
private val refreshButtonEnabled = mutableStateOf(true)
private var locationMessage = mutableStateOf("")

/**
 *
 * Displays the current address of the device and a list of favorite places.
 * Also allows the user to remove a favorite from the database.
 * Also allows the user to swipe an exiting favorite triggering an intent
 * that open's google maps with direction from the current location to the favorite place.
 *
 * @param homeViewModel the view model that retrieves and updates the data
 */
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    ScreenScaffold("Welcome to POIs!") { padding ->
        val context = LocalContext.current
        HomeScreenLayout(homeViewModel, padding)
        // Handle location permission and fetch user location if needed.
        if (requestUserLocation.value) {
            RequestLocationPermission(
                onPermissionGranted = {
                    locationMessage.value = ""
                    homeViewModel.getUserLocation(context, {
                        val prefs = Preferences(context)
                        Log.d("HomeScreen", "Fetched location: ${homeViewModel.location.value}")
                        prefs.searchLongitude = homeViewModel.location.value.lng
                        prefs.searchLatitude = homeViewModel.location.value.lat
                        Log.d("HomeScreen", "Saved to prefs: lat=${prefs.searchLatitude}, lng=${prefs.searchLongitude}")
                        requestUserLocation.value = false
                        refreshButtonEnabled.value = true
                    }, {
                        locationMessage.value = homeViewModel.error.value
                        requestUserLocation.value = false
                        refreshButtonEnabled.value = true
                    })
                },
                onPermissionDenied = {
                    locationMessage.value = "Location Access Denied"
                    requestUserLocation.value = false
                    refreshButtonEnabled.value = true
                }
            )
        }
    }
}

// Home screen layout was extracted into its own method to make the Home Screen logic that deals with user permissions more readable.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenLayout(homeViewModel: HomeViewModel, paddingValues: PaddingValues) {
    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        val userAddress by remember { homeViewModel.address }
        val context = LocalContext.current
        val prefs = Preferences(context)

        // Update location when preferences change.
        LaunchedEffect(prefs.searchLatitude, prefs.searchLongitude) {
            Log.d("HomeScreenLayout", "Prefs location: lat=${prefs.searchLatitude}, lng=${prefs.searchLongitude}")
            if (!(prefs.searchLongitude == 0.0 && prefs.searchLatitude == 0.0)) {
                val location = Location(prefs.searchLatitude ?: 0.0, prefs.searchLongitude ?: 0.0)
                Log.d("HomeScreenLayout", "Using location: $location")
                homeViewModel.getUserAddress(context, location, onSuccess = {
                    locationMessage.value = "Address fetched successfully"
                }, onFailure = {
                    locationMessage.value = homeViewModel.error.value
                })
            } else {
                Log.d("HomeScreenLayout", "Default location (0,0) used")
            }
        }

        val (locationTitle, address1, address2, refreshButton, message, progressIndicator, favoritesList) = createRefs()

        Text(
            modifier = Modifier.constrainAs(locationTitle) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top, margin = 16.dp)
            },
            text = "Current Location:",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            modifier = Modifier.constrainAs(address1) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(locationTitle.bottom, margin = 8.dp)
            },
            text = userAddress.street,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.constrainAs(address2) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(address1.bottom, margin = 4.dp)
            },
            text = userAddress.cityStateZip,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.constrainAs(message) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(address2.bottom, margin = 8.dp)
            },
            text = locationMessage.value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        // Button to refresh the user's location.
        Button(
            enabled = refreshButtonEnabled.value,
            onClick = {
                refreshButtonEnabled.value = false
                requestUserLocation.value = true
            },
            modifier = Modifier.constrainAs(refreshButton) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(message.bottom, margin = 16.dp)
            }
        ) {
            Text("Refresh Location")
        }

        // Show loading indicator when refreshing location.
        if (!refreshButtonEnabled.value) {
            CircularProgressIndicator(
                strokeWidth = 5.dp,
                modifier = Modifier
                    .width(48.dp)
                    .constrainAs(progressIndicator) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(refreshButton.bottom, margin = 16.dp)
                    }
            )
        }

        val favoritePlaces by homeViewModel.favoritePlaces.collectAsState()

        // Display a list of favorite places.
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .constrainAs(favoritesList) {
                    top.linkTo(refreshButton.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
        ) {
            items(favoritePlaces) { place ->
                val currentPlace = rememberUpdatedState(place)
                val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = { state ->
                    if(state == SwipeToDismissBoxValue.StartToEnd){
                        // When the user swipes right, create an intent to open Google Maps with directions from current location to favorite place's location
                        val gmmIntentUri =
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${prefs.searchLatitude},${prefs.searchLongitude}&destination=${currentPlace.value.latitude},${currentPlace.value.longitude}&dir_action=navigate")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }
                    false
                })
                // Swipe gesture detection made easy with Material Design component
                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromEndToStart = false,
                    backgroundContent = {
                        val color by animateColorAsState(MaterialTheme.colorScheme.surfaceContainer)
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(12.dp, 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ){
                            Icon(
                                imageVector = Icons.Outlined.Place, contentDescription = "Directions"
                            )
                            Text("Get Directions")
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Directions"
                            )
                        }
                    }
                ) {
                    FavoritePlaceItem(place = place, onUnfavorite = { homeViewModel.unfavoritePlace(it) })
                }
            }
        }
    }
}

@Composable
fun FavoritePlaceItem(place: PlaceEntity, onUnfavorite: (PlaceEntity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            // Icon button to unfavorite a place.
            IconButton(onClick = { onUnfavorite(place) }) {
                Icon(Icons.Filled.Favorite, contentDescription = "Unfavorite", tint = MaterialTheme.colorScheme.primary)
            }
            Column {
                // Show the name and vicinity of the favorite place.
                Text(text = place.name, style = MaterialTheme.typography.titleMedium)
                Text(text = place.vicinity, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
