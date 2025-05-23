package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.compose.ui.geometry.Rect
import com.google.mlkit.vision.text.Text

sealed interface PartialScreenModeAction {
    data class OnChange(val newText: Text?) : PartialScreenModeAction
    data class OnChangeTextVisibility(val newState: Boolean) : PartialScreenModeAction
    data class SetCaptureRegion(val region: Rect) : PartialScreenModeAction
    data object OnReset : PartialScreenModeAction
}