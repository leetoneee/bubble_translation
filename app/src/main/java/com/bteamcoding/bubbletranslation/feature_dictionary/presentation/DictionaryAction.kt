package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder


sealed interface DictionaryAction {
    data class Search(val query: String) : DictionaryAction
    data class UpdateQuery(val query: String) : DictionaryAction
    data object ClearSearch : DictionaryAction
    // Add more actions as needed
    data object OnLoadAllFolders : DictionaryAction

    data object OnShowAddFolder : DictionaryAction
    data object OnHideAddFolder : DictionaryAction
    data object OnAddNewFolder : DictionaryAction

    data class OnFolderNameChanged(val name: String) : DictionaryAction

    data object OnShowAddWord : DictionaryAction
    data object OnHideAddWord : DictionaryAction
    data class OnAddNewWord(val folder: Folder) : DictionaryAction

    data object ClearError : DictionaryAction
}

