# POIs (Points of Interest) Android App

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Project Structure](#project-structure)
4. [Setup and Installation](#setup-and-installation)
5. [Usage Guide](#usage-guide)
6. [Technical Details](#technical-details)


## Introduction

POIs (Points of Interest) is an Android application developed using Kotlin and Jetpack Compose. The app allows users to search for nearby points of interest based on their current location or a specified location. Users can save their favorite places and manage them within the app.

## Features

- Search for nearby points of interest
- View current location
- Save and manage favorite places
- Customizable search radius
- Integration with Google Maps API
- Modern UI using Jetpack Compose

## Project Structure

The project follows a typical Android app structure with MVVM (Model-View-ViewModel) architecture. Here's an overview of the main components:

- `MainActivity.kt`: The entry point of the application, handling navigation between screens.
- `views/`: Contains the UI components for different screens (Home, Search, Settings).
- `viewmodels/`: Houses the ViewModels for each screen, managing the business logic and data flow.
- `models/`: Defines data classes for various entities used in the app.
- `data/`: Includes Room database related files for local storage of favorite places.
- `api/`: Contains the Retrofit setup for API calls to Google Places.
- `util/`: Utility classes, including shared preferences management.
- `ui/theme/`: Defines the app's theme, colors, and typography.
- `sharedui/`: Reusable UI components and permission handling.

## Setup and Installation

1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Ensure you have the latest Android SDK and Kotlin plugin installed.
4. In the `local.properties` file, add your Google Places API key:
   ```
   GOOGLE_PLACES_API_KEY=your_api_key_here
   ```
5. Build and run the app on an emulator or physical device.

## Usage Guide

### Home Screen
- Displays the current location.
- Shows a list of favorite places near the current location.
- Use the "Refresh Location" button to update your current location.

### Search Screen
- Click "Search Nearby Places" to find points of interest around your location.
- The search uses the radius set in the Settings screen.
- Tap the heart icon to add or remove a place from your favorites.

### Settings Screen
- Adjust the search radius using the slider (0 to 50,000 meters).
- Click "Save" to apply the new search radius.

### Navigation
- Use the bottom navigation bar to switch between Home, Search, and Settings screens.

## Technical Details

### Key Technologies and Libraries
- Kotlin
- Jetpack Compose for UI
- Room for local database
- Retrofit for API calls
- Google Places API
- Coroutines for asynchronous programming
- ViewModel and LiveData for MVVM architecture
- Coil for image loading

### Architecture
The app follows the MVVM (Model-View-ViewModel) architecture:
- Models: Data classes in the `models/` directory
- Views: Compose UI in the `views/` directory
- ViewModels: Business logic in the `viewmodels/` directory

### Data Flow

The POIs app follows a specific data flow for fetching, storing, and displaying nearby places. Here's a detailed breakdown of how data moves through the system:

1. **Fetching Nearby Places**
   - Triggered by: User tapping "Search Nearby Places" on the Search screen
   - Flow:
     a. `SearchViewModel` calls `fetchNearbyPlaces()`
     b. `RetrofitInstance.api.getNearbyPlaces()` makes an API call to Google Places
     c. Response is parsed into a list of `Place` objects
     d. `SearchViewModel.places` is updated with the new list

2. **Saving a Favorite Place**
   - Triggered by: User tapping the heart icon on a place in the Search screen
   - Flow:
     a. `SearchViewModel.toggleFavorite()` is called
     b. Place data is converted to a `PlaceEntity`
     c. `PlaceRepository.insertFavoritePlace()` is called
     d. `PlaceDao` inserts the `PlaceEntity` into the Room database

3. **Displaying Favorite Places**
   - Triggered by: User navigating to the Home screen or refreshing favorites
   - Flow:
     a. `HomeViewModel` initializes or refreshes with `refreshFavoritePlaces()`
     b. `PlaceRepository.getAllFavoritePlaces()` is called
     c. `PlaceDao` fetches all favorite places from the Room database
     d. `HomeViewModel._favoritePlaces` StateFlow is updated
     e. `HomeScreen` observes `favoritePlaces` and updates the UI

4. **Removing a Favorite Place**
   - Triggered by: User tapping the heart icon on a favorited place
   - Flow:
     a. `HomeViewModel.unfavoritePlace()` is called
     b. `PlaceRepository.deleteFavoritePlace()` is called
     c. `PlaceDao` removes the place from the Room database
     d. `HomeViewModel` refreshes the favorites list

5. **Updating Search Radius**
   - Triggered by: User changing the radius in the Settings screen
   - Flow:
     a. `SettingsViewModel.setSearchRadius()` is called
     b. New radius is stored in `SharedPreferences`
     c. Next search uses the updated radius from `SharedPreferences`

This data flow ensures that:
- The app always displays the most up-to-date information from the API
- User preferences (like favorites and search radius) are persisted locally
- The UI reactively updates based on changes in the underlying data

The use of ViewModels, StateFlows, and Compose's state management allows for a reactive and efficient data flow throughout the app. This architecture ensures that data consistency is maintained across different parts of the app and provides a smooth user experience.
