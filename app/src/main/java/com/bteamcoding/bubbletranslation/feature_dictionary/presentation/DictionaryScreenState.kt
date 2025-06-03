package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

data class DictionaryScreenState(
    val searchQuery: String = "",
    val definitions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
