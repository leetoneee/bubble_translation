package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AutoScreenModeViewModel : ViewModel()  {
    private val _state = MutableStateFlow(AutoScreenModeState())
    val state = _state.asStateFlow()

    fun onAction(action: AutoScreenModeAction) {
        when (action) {
            is AutoScreenModeAction.OnChange -> {
                _state.update { it.copy(visionText = action.newText) }
            }

            is AutoScreenModeAction.SetCaptureRegion -> {
                _state.update { it.copy(captureRegion = action.region) }
            }

            AutoScreenModeAction.OnReset -> {
                _state.update { it.copy(visionText = null) }
            }

            is AutoScreenModeAction.OnChangeTextVisibility -> {
                _state.update { it.copy(isTextVisibility = action.newState) }
            }
        }
    }
}