package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word

data class BookmarkState(
    val allFolders: List<Folder> = listOf(),
    val allWords: List<Word> = listOf(),
    val allLocalWords: List<Word> = listOf(),
    val folders: List<Folder> = listOf(),
    val words: List<Word> = listOf(),
    val currentFolder: Folder? = null,
    val tempFolder: Folder? = null,
    val tempWord: Word? = null,
    val searchQuery: String = "",
    val showAddFolderDialog: Boolean = false,
    val showEditFolderDialog: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val showConfirmDeleteWordDialog: Boolean = false,
    val folderName: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
