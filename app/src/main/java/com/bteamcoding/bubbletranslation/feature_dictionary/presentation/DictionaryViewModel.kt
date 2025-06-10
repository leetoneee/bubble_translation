package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.core.utils.callApiForTranslation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            // TODO: Replace with real dictionary lookup
            if (query.isBlank()) {
                _state.value = _state.value.copy(
                    definitions = emptyList(),
                    isLoading = false,
                    error = "Please enter a word."
                )
            } else {
                val text = withContext(Dispatchers.IO) { callApiForTranslation(query) }
                // Fake dictionary result
                val fakeDefinitions = listOf(
                    "$query (n): $text",
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
