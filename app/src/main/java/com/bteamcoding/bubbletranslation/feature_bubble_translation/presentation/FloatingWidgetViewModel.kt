package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import androidx.lifecycle.ViewModel
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

            is FloatingWidgetAction.OnClose -> {
                // Handle close widget logic here if needed
                _state.update {
                    it.copy(isExpanded = false)
                }
            }

            is FloatingWidgetAction.OnModeChange -> {
                _state.update {
                    it.copy(translateMode = action.newMode)
                }
            }
        }
    }
}