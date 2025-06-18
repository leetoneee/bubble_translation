package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components

import androidx.compose.ui.graphics.vector.ImageVector

data class FABItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)