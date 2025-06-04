package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DictionaryViewModel : ViewModel() {
    private val _state = MutableStateFlow(DictionaryScreenState())
    val state = _state.asStateFlow()

    fun onAction(action: DictionaryAction) {
        when (action) {
            is DictionaryAction.Search -> {
                _state.value = _state.value.copy(
                    searchQuery = action.query,
                    isLoading = true,
                    error = null
                )
                searchWord(action.query)
            }
            is DictionaryAction.ClearSearch -> {
                _state.value = _state.value.copy(
                    searchQuery = "",
                    definitions = emptyList(),
                    error = null
                )
            }
            is DictionaryAction.UpdateQuery -> {
                _state.value = _state.value.copy(
                    searchQuery = action.query
                )
            }
        }
    }

    private fun searchWord(query: String) {
        viewModelScope.launch {
            // Simulate loading
            delay(1000)
            // TODO: Replace with real dictionary lookup
            if (query.isBlank()) {
                _state.value = _state.value.copy(
                    definitions = emptyList(),
                    isLoading = false,
                    error = "Please enter a word."
                )
            } else {
                // Fake dictionary result
                val fakeDefinitions = listOf(
                    "$query (n): Định nghĩa tiếng Việt 1",
                    "$query (v): Định nghĩa tiếng Việt 2"
                )
                _state.value = _state.value.copy(
                    definitions = fakeDefinitions,
                    isLoading = false,
                    error = null
                )
            }
        }
    }
}
