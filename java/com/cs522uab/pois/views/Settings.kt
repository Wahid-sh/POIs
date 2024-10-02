package com.cs522uab.pois.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.cs522uab.pois.sharedui.ScreenScaffold
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.cs522uab.pois.viewmodels.SettingsViewModel
import kotlinx.coroutines.delay

/**
 *
 * Displays the current search radius user preference and allows the user to
 * change that preference value with a slider component.
 *
 *
 * @param settingsViewModel the view model that retrieves and updates the data
 */
@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    ScreenScaffold("Configure Settings:"){ padding ->
        ConstraintLayout(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            val context = LocalContext.current
            val searchRadius = settingsViewModel.getSearchRadius(context)
            var sliderPosition by remember { mutableStateOf(searchRadius) }
            val (searchRadiusTitle, searchRadiusMin, searchRadiusMax, searchRadiusSlider, saveButton, progressIndicator) = createRefs()
            Text(
                modifier = Modifier.constrainAs(searchRadiusTitle) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        topMargin = 16.dp,
                        bottom = parent.bottom,
                        bottomMargin = 16.dp,
                        verticalBias = 0.0F
                    )
                },
                text = "Search Radius (meters): ${sliderPosition}",
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
            )
            Text(
                modifier = Modifier.constrainAs(searchRadiusMin) {
                    linkTo(
                        start = parent.start,
                        startMargin = 16.dp,
                        end = parent.end,
                        top = searchRadiusTitle.bottom,
                        topMargin = 48.dp,
                        bottom = parent.bottom,
                        bottomMargin = 16.dp,
                        verticalBias = 0.0F,
                        horizontalBias = 0.0F
                    )
                },
                text = "0"
            )
            Slider(
                modifier = Modifier.padding(horizontal = 16.dp).constrainAs(searchRadiusSlider) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = searchRadiusTitle.bottom,
                        topMargin = 16.dp,
                        bottom = parent.bottom,
                        bottomMargin = 16.dp,
                        verticalBias = 0.0F
                    )
                },
                value = sliderPosition.toFloat(),
                onValueChange = { sliderPosition = it.toInt() },
                steps = 50,
                valueRange = 0F..50000F
            )
            Text(
                    modifier = Modifier.constrainAs(searchRadiusMax) {
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            endMargin = 16.dp,
                            top = searchRadiusTitle.bottom,
                            topMargin = 48.dp,
                            bottom = parent.bottom,
                            bottomMargin = 16.dp,
                            verticalBias = 0.0F,
                            horizontalBias = 1.0F
                        )
                    },
            text = "50000"
            )
            var enabled by remember { mutableStateOf(true) }
            LaunchedEffect(enabled) {
                if (enabled) return@LaunchedEffect
                else delay(1000L)
                enabled = true
            }
            Button(
                enabled = enabled,
                onClick = {
                    enabled = false
                    settingsViewModel.setSearchRadius(context, sliderPosition)
                },
                modifier = Modifier.constrainAs(saveButton) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        bottom = parent.bottom,
                        verticalBias = 1.0F
                    )
                }
            ) {
                Text("Save")
            }
            if (!enabled) CircularProgressIndicator(
                strokeWidth = 5.dp,
                modifier = Modifier.width(48.dp).constrainAs(progressIndicator) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        bottom = parent.bottom
                    )
                }
            )
        }
    }
}

