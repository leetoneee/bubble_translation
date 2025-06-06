package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AudioModeViewModel : ViewModel() {
    private val _state = MutableStateFlow(AudioModeState())
    val state = _state.asStateFlow()

    fun onAction(action: AudioModeAction) {
        when (action) {
            is AudioModeAction.OnChangeText -> {
                _state.update { it.copy(recognizedText = action.newText) }
            }

            is AudioModeAction.OnReset ->
                _state.update {
                    it.copy(
                        recognizedText = "",
                        isRecognizing = false,
                        topPosition = 0,
                        isTranslateMode = true
                    )
                }

            is AudioModeAction.OnChangeIsRecognizing -> {
                _state.update { it.copy(isRecognizing = action.newState) }
            }

            is AudioModeAction.OnChangePosition -> {
                _state.update { it.copy(topPosition = action.newPosition) }
            }

            is AudioModeAction.OnChangeIsTranslateMode -> {
                _state.update { it.copy(isTranslateMode = !it.isTranslateMode) }
            }
        }
    }
}