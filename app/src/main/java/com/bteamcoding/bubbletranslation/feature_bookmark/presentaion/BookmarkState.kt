package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word

data class BookmarkState(
    val folders: List<Folder> = listOf(),
    val words: List<Word> = listOf(),
    val currentFolder: Folder? = null,
    val searchQuery: String = "",
    val showAddFolderDialog: Boolean = false,
    val folderName: String = "",
    val errorMessage: String? = null,
)
