package com.bteamcoding.bubbletranslation.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
    ) {
        composable(route = NavRoutes.HOME) {

        }
        composable(route = NavRoutes.CAPTURE) {

        }
        composable(route = NavRoutes.DICTIONARY) {

        }
        composable(route = NavRoutes.FLASH_CARD) {

        }
    }
}