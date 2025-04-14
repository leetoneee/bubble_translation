package com.bteamcoding.bubbletranslation.app.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.app.navigation.BottomNavItem
import com.bteamcoding.bubbletranslation.app.navigation.NavRoutes

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = NavRoutes.HOME,
            title = "Home",
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = NavRoutes.CAPTURE,
            title = "Capture",
            selectedIcon = Icons.Default.Camera,
            unselectedIcon = Icons.Outlined.Camera
        ),
        BottomNavItem(
            route = NavRoutes.DICTIONARY,
            title = "Dictionary",
            selectedIcon = ImageVector.vectorResource(R.drawable.outline_book_open_24),
            unselectedIcon = ImageVector.vectorResource(R.drawable.outline_book_default_24)
        ),
        BottomNavItem(
            route = NavRoutes.FLASH_CARD,
            title = "Bookmark",
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = ImageVector.vectorResource(R.drawable.outline_bookmark_border_24)
        )
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                alwaysShowLabel = false,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = if (currentRoute == item.route) colorResource(R.color.blue_900) else LocalContentColor.current
                    )
                },
                label = { Text(item.title) },
                colors = NavigationBarItemColors(
                    selectedIconColor = colorResource(R.color.blue_900),
                    unselectedIconColor = LocalContentColor.current,
                    selectedTextColor = colorResource(R.color.blue_900),
                    unselectedTextColor = LocalContentColor.current,
                    disabledIconColor = LocalContentColor.current,
                    disabledTextColor = LocalContentColor.current,
                    selectedIndicatorColor = colorResource(R.color.blue_100)
                )
            )

        }
    }
}