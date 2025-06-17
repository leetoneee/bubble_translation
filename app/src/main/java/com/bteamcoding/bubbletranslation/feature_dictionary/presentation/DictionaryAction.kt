package com.bteamcoding.bubbletranslation.feature_dictionary.presentation



sealed interface DictionaryAction {
    data class Search(val query: String) : DictionaryAction
    data class UpdateQuery(val query: String) : DictionaryAction
    data object ClearSearch : DictionaryAction
    // Add more actions as needed
    data object OnShowAddFolder : DictionaryAction
    data object OnHideAddFolder : DictionaryAction
    data object OnAddNewFolder : DictionaryAction

    data class OnFolderNameChanged(val name: String) : DictionaryAction

    data object ClearError : DictionaryAction
}

