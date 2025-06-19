package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.google.mlkit.vision.text.Text

data class FullscreenModeState(
    val visionText: Text? = null,
    val translatedVisionText: TranslatedVisionText? = null,
    val isTextVisibility: Boolean = false
)