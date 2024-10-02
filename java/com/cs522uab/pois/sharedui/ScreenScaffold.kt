package com.cs522uab.pois.sharedui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A reusable scaffold that contains a simple top bar with a title
 *
 * @param title The title that should show at the top of the screen.
 * @param content The rest of the screen content to be rendered within this scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(title: String, content: @Composable (PaddingValues) -> Unit){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        content = content
    )
}