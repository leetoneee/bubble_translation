package com.bteamcoding.bubbletranslation.feature_camera.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CameraScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(CameraScreenState())
    val state = _state.asStateFlow()

    fun onAction(action: CameraScreenAction) {
        when (action) {
            is CameraScreenAction.SetUri -> {
                _state.update { it.copy(photoUri = action.uri) }
            }

            is CameraScreenAction.OnReset -> {
                _state.update { it.copy(photoUri = null) }
            }
        }
    }
}