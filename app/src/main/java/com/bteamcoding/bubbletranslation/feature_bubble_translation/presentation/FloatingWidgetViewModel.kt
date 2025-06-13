package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.lifecycle.ViewModel
import com.bteamcoding.bubbletranslation.core.utils.LanguageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FloatingWidgetViewModel() : ViewModel() {
    private val _state = MutableStateFlow(FloatingWidgetState())
    val state = _state.asStateFlow()

    fun onAction(action: FloatingWidgetAction) {
        when (action) {
            is FloatingWidgetAction.OnToggleExpand -> {
                _state.update { it.copy(isExpanded = !it.isExpanded) }
            }
            is FloatingWidgetAction.OnStart -> {
                // Handle start widget logic here if needed
                _state.update {
                    it.copy(isOn = true)
                }
            }

            is FloatingWidgetAction.OnClose -> {
                // Handle close widget logic here if needed
                _state.update {
                    it.copy(isExpanded = false, isOn = false)
                }
            }

            is FloatingWidgetAction.OnModeChange -> {
                _state.update {
                    it.copy(translateMode = action.newMode)
                }
            }

            is FloatingWidgetAction.OnSourceLanguageChange -> {
                // Cập nhật state và gọi LanguageManager để thay đổi ngôn ngữ nguồn
                _state.update { it.copy(sourceLanguage = action.newSourceLanguage) }
                LanguageManager.updateSourceLanguage(action.newSourceLanguage)
            }

            is FloatingWidgetAction.OnTargetLanguageChange -> {
                // Cập nhật state và gọi LanguageManager để thay đổi ngôn ngữ đích
                _state.update { it.copy(targetLanguage = action.newTargetLanguage) }
                LanguageManager.updateTargetLanguage(action.newTargetLanguage)
            }
        }
    }
}