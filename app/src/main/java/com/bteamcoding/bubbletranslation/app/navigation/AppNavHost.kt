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
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.DictionaryScreenRoot
import androidx.compose.runtime.State
import androidx.navigation.toRoute
import com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.BookmarkScreenRoot
import com.bteamcoding.bubbletranslation.app.presentation.SplashScreen
import com.bteamcoding.bubbletranslation.app.presentation.SplashScreen
import com.bteamcoding.bubbletranslation.feature_dictionary.presentation.DictionaryViewModel
import com.bteamcoding.bubbletranslation.feature_home.presentation.HomeScreenRoot
import kotlinx.serialization.Serializable
import kotlinx.coroutines.delay

@Composable
fun AppNavHost(
    navController: NavHostController,
    onRequestScreenCapturePermission: () -> Unit,
    onPermissionGranted: () -> Unit,
    permissionGranted: State<Boolean>
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
    ) {
        navigation(
            startDestination = NavRoutes.ACCOUNT,
            route = NavRoutes.AUTH
        ) {

            composable(route = NavRoutes.ACCOUNT) {
                val viewModel = it.sharedHiltViewModel<AuthViewModel>(navController)

                AccountScreenRoot(
                    viewModel = viewModel,
                    onNavToProfileScreen = {
                        navController.navigate(NavRoutes.PROFILE)
                    },
                    onNavToLoginScreen = {
                        navController.navigate(NavRoutes.LOGIN)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(route = NavRoutes.LOGIN) {
                val viewModel = it.sharedHiltViewModel<AuthViewModel>(navController)

                LoginScreenRoot(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavToRegisterScreen = {
                        navController.navigate(NavRoutes.REGISTER)
                    }
                )
            }
            composable(route = NavRoutes.REGISTER) {
                val viewModel = it.sharedHiltViewModel<AuthViewModel>(navController)

                RegisterScreenRoot(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(route = NavRoutes.PROFILE) {
                val viewModel = it.sharedHiltViewModel<AuthViewModel>(navController)

                ProfileScreenRoot(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(route = NavRoutes.HOME) {
            val startFWUseCase = StartFloatingWidgetUseCase(LocalContext.current)

            HomeScreenRoot(
                startUseCase = startFWUseCase,
                onRequestScreenCapturePermission = onRequestScreenCapturePermission,
                permissionGranted = permissionGranted,
                navController = navController
            )
        }
        composable(route = NavRoutes.CAPTURE) {

            CameraScreenRoot(
                navController = navController
            )
        }
        composable(route = NavRoutes.DICTIONARY) {
            DictionaryScreenRoot(
                navController = navController
            )
        }
        composable<DictionaryScreenParams> {
            val args = it.toRoute<DictionaryScreenParams>()

            DictionaryScreenRoot(
                navController = navController,
                searchQuery = args.searchQuery,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = NavRoutes.FLASH_CARD) {
            BookmarkScreenRoot(
                navController = navController
            )
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

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedHiltViewModel(navController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}

@Serializable
data class DictionaryScreenParams(
    val searchQuery: String? =null
)