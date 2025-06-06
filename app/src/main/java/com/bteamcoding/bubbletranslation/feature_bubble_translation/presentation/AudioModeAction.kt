package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation


sealed interface AudioModeAction {
    data class OnChangeText(val newText: String) : AudioModeAction
    data class OnChangeIsRecognizing(val newState: Boolean) : AudioModeAction
    data class OnChangePosition(val newPosition: Int): AudioModeAction
    data object OnChangeIsTranslateMode : AudioModeAction
    data object OnReset : AudioModeAction
}