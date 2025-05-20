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
            is PreviewImageAction.OnChange -> {
                _state.update { it.copy(visionText = action.newText) }
            }

            is PreviewImageAction.OnChangeTextVisibility -> {
                _state.update { it.copy(isTextVisibility = action.newState) }
            }

            is PreviewImageAction.SetImageBitmap -> {
                _state.update { it.copy(imageBitmap = action.newBitmap) }
            }

            is PreviewImageAction.OnChangeTranslatedVisionText -> {
                _state.update {
                    it.copy(translatedVisionText = action.newText)
                }
            }

            is PreviewImageAction.OnReset -> {
                _state.update {
                    it.copy(
                        visionText = null,
                        imageBitmap = null,
                        isTextVisibility = false
                    )
                }
            }

            is PreviewImageAction.OnChangeIsSpeaking -> {
                _state.update { it.copy(isSpeaking = action.newState) }
            }
        }
    }
}