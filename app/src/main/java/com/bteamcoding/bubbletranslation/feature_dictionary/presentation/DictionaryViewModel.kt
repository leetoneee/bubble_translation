package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DictionaryViewModel : ViewModel() {
    private val _state = MutableStateFlow(DictionaryScreenState())
    val state = _state.asStateFlow()

    fun onAction(action: DictionaryAction) {
        when (action) {
            is DictionaryAction.Search -> {
                _state.value = _state.value.copy(searchQuery = action.query)
                // TODO: Add search logic here
            }
            is DictionaryAction.ClearSearch -> {
                _state.value = _state.value.copy(searchQuery = "")
            }
        }
    }
}

