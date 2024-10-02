package com.cs522uab.pois

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.cs522uab.pois.ui.theme.POIsTheme
import com.cs522uab.pois.views.SettingsScreen
import com.cs522uab.pois.views.HomeScreen
import com.cs522uab.pois.views.SearchScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs522uab.pois.viewmodels.HomeViewModel
import com.cs522uab.pois.viewmodels.SearchViewModel
import com.cs522uab.pois.viewmodels.SettingsViewModel
import com.cs522uab.pois.data.AppDatabase
import com.cs522uab.pois.data.PlaceRepository
import com.cs522uab.pois.viewmodels.HomeViewModelFactory
import com.cs522uab.pois.viewmodels.SearchViewModelFactory
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: PlaceRepository

    // Setting up the view models for different screens.
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(repository)
    }

    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(repository) { homeViewModel.refreshFavoritePlaces() }
    }

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the database and repository for accessing data.
        database = AppDatabase.getDatabase(applicationContext)
        repository = PlaceRepository(database.placeDao())

        // Add the main activity's scaffolding including the navigation bar
        setContent {
            POIsTheme {
                val navController = rememberNavController() // Manages navigation between screens.
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NavigationBarPOIs(navController) } // Adds a bottom navigation bar.
                ) { padding ->
                    // Define navigation routes and which composable to show.
                    NavHost(
                        modifier = Modifier.padding(padding),
                        navController = navController,
                        startDestination = Home.route
                    ) {
                        composable(Settings.route) {
                            SettingsScreen(settingsViewModel)
                        }
                        composable(Home.route) {
                            HomeScreen(homeViewModel)
                        }
                        composable(Search.route) {
                            SearchScreen(searchViewModel)
                        }
                    }
                }
            }
        }
    }
}

// Use serializable objects as navigation routes for each screen.  This is type safe as compared to passing strings that are checked by the compiler.
@Serializable
object Settings {
    const val route = "settings"
}

@Serializable
object Home {
    const val route = "home"
}

@Serializable
object Search {
    const val route = "search"
}

// Holds info for navigation bar items.
data class NavigationBarItemPOIs(
    val icon: ImageVector,
    val selected: ImageVector,
    val label: String,
    val route: String
)

// A function that defines our navigation bar and configures the navigation controller with routes to our three screens.
@Composable
fun NavigationBarPOIs(
    navController: NavHostController
) {
    var selectedItem by remember { mutableIntStateOf(1) }
    val items = listOf(
        NavigationBarItemPOIs(Icons.Outlined.Settings, Icons.Filled.Settings, "Settings", Settings.route),
        NavigationBarItemPOIs(Icons.Outlined.Home, Icons.Filled.Home, "Home", Home.route),
        NavigationBarItemPOIs(Icons.Outlined.Search, Icons.Filled.Search, "Search", Search.route)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) item.selected else item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
