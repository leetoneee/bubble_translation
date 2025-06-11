package com.bteamcoding.bubbletranslation.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.bteamcoding.bubbletranslation.feature_auth.presentation.AccountScreenRoot
import com.bteamcoding.bubbletranslation.feature_auth.presentation.AuthViewModel
import com.bteamcoding.bubbletranslation.feature_auth.presentation.LoginScreenRoot
import com.bteamcoding.bubbletranslation.feature_auth.presentation.ProfileScreenRoot
import com.bteamcoding.bubbletranslation.feature_auth.presentation.RegisterScreenRoot
import com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case.StartFloatingWidgetUseCase
import com.bteamcoding.bubbletranslation.feature_camera.presentation.CameraScreenRoot
import com.bteamcoding.bubbletranslation.feature_home.presentation.HomeScreenRoot


@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
    ) {
        navigation(
            startDestination = NavRoutes.ACCOUNT,
            route = NavRoutes.AUTH
        ) {

            composable(route = NavRoutes.ACCOUNT) {
                val viewModel = hiltViewModel<AuthViewModel>(it)

                AccountScreenRoot(
                    viewModel = viewModel,
                    onNavToProfileScreen = {},
                    onNavToLoginScreen = {}
                )
            }
            composable(route = NavRoutes.LOGIN) {
                val viewModel = hiltViewModel<AuthViewModel>(it)

                LoginScreenRoot(
                    viewModel = viewModel,
                )
            }
            composable(route = NavRoutes.REGISTER) {
                val viewModel = hiltViewModel<AuthViewModel>(it)

                RegisterScreenRoot(
                    viewModel = viewModel,

                    )
            }
            composable(route = NavRoutes.PROFILE) {
                val viewModel = hiltViewModel<AuthViewModel>(it)

                ProfileScreenRoot(
                    viewModel = viewModel
                )
            }
        }
        composable(route = NavRoutes.HOME) {
            val startFWUseCase = StartFloatingWidgetUseCase(LocalContext.current)

            HomeScreenRoot(
                startUseCase = startFWUseCase
            )
        }
        composable(route = NavRoutes.CAPTURE) {

            CameraScreenRoot(
                navController = navController
            )
        }
        composable(route = NavRoutes.DICTIONARY) {

        }
        composable(route = NavRoutes.FLASH_CARD) {

        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
