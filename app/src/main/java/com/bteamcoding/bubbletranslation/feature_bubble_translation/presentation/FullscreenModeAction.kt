package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.google.mlkit.vision.text.Text

sealed interface FullscreenModeAction {
    data class OnChange(val newText: Text) : FullscreenModeAction
    data class OnChangeTextVisibility(val newState: Boolean) : FullscreenModeAction
    data class OnChangeTranslatedVisionText(val newText: TranslatedVisionText) : FullscreenModeAction
    data object OnReset : FullscreenModeAction
}