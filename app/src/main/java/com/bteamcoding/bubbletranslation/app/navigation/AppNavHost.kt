package com.bteamcoding.bubbletranslation.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case.StartFloatingWidgetUseCase
import com.bteamcoding.bubbletranslation.feature_camera.presentation.CameraScreenRoot
import com.bteamcoding.bubbletranslation.feature_home.presentation.HomeScreenRoot

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
    ) {
        composable(route = NavRoutes.HOME) {
            val startFWUseCase = StartFloatingWidgetUseCase(LocalContext.current)

            HomeScreenRoot(
                startUseCase = startFWUseCase
            )
        }
        composable(route = NavRoutes.CAPTURE) {

            CameraScreenRoot()
        }
        composable(route = NavRoutes.DICTIONARY) {

        }
        composable(route = NavRoutes.FLASH_CARD) {

        }
    }
}