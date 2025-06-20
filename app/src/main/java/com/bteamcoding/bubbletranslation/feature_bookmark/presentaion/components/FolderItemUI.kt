package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion.components

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder

data class FolderItemUI(
    val folder: Folder,
    val isOptionsRevealed: Boolean
)
