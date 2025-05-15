package com.bteamcoding.bubbletranslation.feature_camera.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PreviewImageViewModel : ViewModel() {
    private val _state = MutableStateFlow(PreviewImageState())
    val state = _state.asStateFlow()

    fun onAction(action: PreviewImageAction) {
        when (action) {
            is PreviewImageAction.SetUri -> {
                _state.update { it.copy(photoUri = action.uri) }
            }

            is PreviewImageAction.OnReset -> {
                _state.update { it.copy(photoUri = null) }
            }
        }
    }
}