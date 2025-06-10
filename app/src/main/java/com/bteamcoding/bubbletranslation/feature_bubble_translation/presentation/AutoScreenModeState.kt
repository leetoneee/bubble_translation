package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.compose.ui.geometry.Rect
import com.bteamcoding.bubbletranslation.core.utils.ScreenSizeHolder
import com.google.mlkit.vision.text.Text

data class AutoScreenModeState(
    var visionText: Text? = null,
    val captureRegion: Rect = Rect(0f, 100f, ScreenSizeHolder.screenWidth, 400f),
    val isTextVisibility: Boolean = false
)
