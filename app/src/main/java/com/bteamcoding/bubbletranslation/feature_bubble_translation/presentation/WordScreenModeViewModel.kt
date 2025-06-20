package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WordScreenModeViewModel : ViewModel() {
    private val _state = MutableStateFlow(WordScreenModeState())
    val state = _state.asStateFlow()

    fun onAction(action: WordScreenModeAction) {
        when (action) {
            is WordScreenModeAction.OnChangeSourceText -> {
                _state.update { it.copy(sourceText = action.newSourceText) }
            }

            is WordScreenModeAction.OnChangeTranslatedText -> {
                _state.update { it.copy(translatedText = action.newTranslatedText) }
            }

            is WordScreenModeAction.OnShowBottomSheet -> {
                _state.update { it.copy(isShowBottomSheet = action.value) }
            }

            WordScreenModeAction.OnReset -> {
                _state.update { it.copy(sourceText = null) }
            }
        }
    }
}