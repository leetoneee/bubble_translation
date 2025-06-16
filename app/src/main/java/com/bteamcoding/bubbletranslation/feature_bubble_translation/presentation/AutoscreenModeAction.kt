package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.compose.ui.geometry.Rect
import com.google.mlkit.vision.text.Text

interface AutoScreenModeAction {
    data class OnChange(val newText: Text?) : AutoScreenModeAction
    data class OnChangeTextVisibility(val newState: Boolean) : AutoScreenModeAction
    data class SetCaptureRegion(val region: Rect) : AutoScreenModeAction
    data object OnReset : AutoScreenModeAction
}