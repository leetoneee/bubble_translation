package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word


sealed interface BookmarkAction {
    data object OnLoadCurrentUser : BookmarkAction

    data object OnLoadAllFolders : BookmarkAction
    data class OnLoadWordsByFolder(val folderId: String) : BookmarkAction
    data object OnClearFolders : BookmarkAction
    data class OnFolderClick(val folder: Folder) : BookmarkAction

    data object OnShowAddFolder : BookmarkAction
    data object OnHideAddFolder : BookmarkAction
    data object OnAddNewFolder : BookmarkAction

    data class OnFolderNameChanged(val name: String) : BookmarkAction
    data class OnSetTempFolder(val folder: Folder) : BookmarkAction

    data class OnShowEditFolder(val folder: Folder) : BookmarkAction
    data object OnHideEditFolder : BookmarkAction
    data object OnUpdateFolderName : BookmarkAction

    data class OnShowConfirm(val folder: Folder) : BookmarkAction
    data object OnHideConfirm : BookmarkAction
    data object OnDeleteFolder : BookmarkAction

    data class OnShowConfirmDeleteWord(val word: Word) : BookmarkAction
    data object OnHideConfirmDeleteWord : BookmarkAction
    data object OnDeleteWord : BookmarkAction

    data class OnQueryChanged(val query: String) : BookmarkAction

    data object ClearError : BookmarkAction
    data object OnSync : BookmarkAction
}