package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

sealed interface DictionaryAction {
    data class Search(val query: String) : DictionaryAction
    data class UpdateQuery(val query: String) : DictionaryAction
    object ClearSearch : DictionaryAction
    // Add more actions as needed
}

