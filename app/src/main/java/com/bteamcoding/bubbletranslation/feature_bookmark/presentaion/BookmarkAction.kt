package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder


sealed interface BookmarkAction {
    data object OnLoadCurrentUser : BookmarkAction

    data object OnLoadAllFolders : BookmarkAction
    data class OnLoadWordsByFolder(val folderId: String) : BookmarkAction
    data class OnUpdateFolderName(val folderId: String,val name: String) : BookmarkAction
    data class OnDeleteFolder(val folderId: String) : BookmarkAction
    data object OnClearFolders : BookmarkAction
    data class OnFolderClick(val folder: Folder) : BookmarkAction

    data object OnShowAddFolder : BookmarkAction
    data object OnHideAddFolder : BookmarkAction
    data object OnAddNewFolder : BookmarkAction
    data class OnFolderNameChanged(val name: String) : BookmarkAction

    data class OnQueryChanged(val query: String) : BookmarkAction

    data object ClearError : BookmarkAction
}