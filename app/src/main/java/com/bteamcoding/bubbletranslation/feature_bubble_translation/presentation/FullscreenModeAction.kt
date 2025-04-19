package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.google.mlkit.vision.text.Text

sealed interface FullscreenModeAction {
    data class OnChange(val newText: Text) : FullscreenModeAction
    data object OnReset : FullscreenModeAction
}