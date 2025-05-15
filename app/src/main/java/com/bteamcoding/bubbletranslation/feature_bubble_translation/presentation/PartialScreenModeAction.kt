package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.google.mlkit.vision.text.Text

sealed interface PartialScreenModeAction {
    data class OnChange(val newText: Text) : PartialScreenModeAction
    data object OnReset : PartialScreenModeAction
}