package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.compose.ui.geometry.Rect
import com.google.mlkit.vision.text.Text

sealed interface WordScreenModeAction {
    data class OnChangeSourceText(val newSourceText: String?) : WordScreenModeAction
    data class OnChangeTranslatedText(val newTranslatedText: String?) : WordScreenModeAction
    data class OnShowBottomSheet(val value: Boolean) : WordScreenModeAction
    data object OnReset : WordScreenModeAction
}