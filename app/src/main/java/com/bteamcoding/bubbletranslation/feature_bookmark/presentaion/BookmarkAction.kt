package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion


sealed interface BookmarkAction {
    data object OnLoadCurrentUser : BookmarkAction

    data object OnLoadAllFolders : BookmarkAction
    data class OnLoadWordsByFolder(val folderId: String) : BookmarkAction
    data class OnAddNewFolder(val name: String) : BookmarkAction
    data class OnUpdateFolderName(val folderId: String,val name: String) : BookmarkAction
    data class OnDeleteFolder(val folderId: String) : BookmarkAction
    data object OnClearFolders : BookmarkAction
}