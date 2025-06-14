package com.bteamcoding.bubbletranslation.app.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.bteamcoding.bubbletranslation.app.navigation.AppNavHost
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState


@Composable
fun MainScreen(
    onRequestScreenCapturePermission: () -> Unit,
    onPermissionGranted: () -> Unit,
    permissionGranted: State<Boolean>
) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = { BottomBar(navController) }
        ) { paddingValues: PaddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavHost(
                    navController,
                    onRequestScreenCapturePermission,
                    onPermissionGranted,
                    permissionGranted
                )
            }
        }
    }
}

@Composable
@Preview
fun MainScreenPreview() {
    val fakePermissionState: State<Boolean> = rememberUpdatedState(true)

    MainScreen(
        onRequestScreenCapturePermission = {},
        onPermissionGranted = {},
        permissionGranted = fakePermissionState
    )
}