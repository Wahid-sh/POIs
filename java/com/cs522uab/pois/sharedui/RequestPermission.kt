package com.cs522uab.pois.sharedui

import androidx.compose.runtime.Composable
import android.Manifest
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// https://medium.com/@munbonecci/how-to-get-your-location-in-jetpack-compose-f085031df4c1
/**
 * A simplified version of the function to request location permissions.
 *
 * @param onPermissionGranted Callback to be executed when the requested permission is granted.
 * @param onPermissionDenied Callback to be executed when the requested permission is denied.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied:  () -> Unit
) {
    // We are only using the fine location in this version of the app
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Use LaunchedEffect to handle permissions logic when the composition is launched.
    LaunchedEffect(key1 = permissionState) {
        if (!permissionState.status.isGranted) permissionState.launchPermissionRequest()
        if (permissionState.status.isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }
}