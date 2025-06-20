package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.google.mlkit.vision.text.Text
import androidx.compose.ui.geometry.Rect
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText

data class PartialScreenModeState(
    var visionText: Text? = null,
    val translatedVisionText: TranslatedVisionText? = null,
    val captureRegion: Rect = Rect(0f, 0f, 300f, 400f),
    val isTextVisibility: Boolean = false
)
