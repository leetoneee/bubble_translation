package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation


sealed interface AudioModeAction {
    data class OnChangeText(val newText: String) : AudioModeAction
    data class OnChangeIsRecognizing(val newState: Boolean) : AudioModeAction
    data object OnReset : AudioModeAction
}