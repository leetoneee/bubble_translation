package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FullscreenModeViewModel : ViewModel() {
    private val _state = MutableStateFlow(FullscreenModeState())
    val state = _state.asStateFlow()

    fun onAction(action: FullscreenModeAction) {
        when (action) {
            is FullscreenModeAction.OnChange -> {
                _state.update { it.copy(visionText = action.newText) }
            }

            FullscreenModeAction.OnReset ->
                _state.update { it.copy(visionText = null) }
        }
    }
}