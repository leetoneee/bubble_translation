package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PartialScreenModeViewModel : ViewModel() {
    private val _state = MutableStateFlow(PartialScreenModeState())
    val state = _state.asStateFlow()

    fun onAction(action: PartialScreenModeAction) {
        when (action) {
            is PartialScreenModeAction.OnChange -> {
                _state.update { it.copy(visionText = action.newText) }
            }

            is PartialScreenModeAction.SetCaptureRegion -> {
                _state.update { it.copy(captureRegion = action.region) }
            }

            PartialScreenModeAction.OnReset -> {
                _state.update { it.copy(visionText = null) }
            }

            is PartialScreenModeAction.OnChangeTranslatedVisionText -> {
                _state.update {
                    it.copy(translatedVisionText = action.newText)
                }
            }

            is PartialScreenModeAction.OnChangeTextVisibility -> {
                _state.update { it.copy(isTextVisibility = action.newState) }
            }
        }
    }
}