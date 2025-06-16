package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.app.domain.use_case.GetUserInfoUseCase
import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.AddFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.DeleteFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetAllFoldersUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.UpdateFolderNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getAllFoldersUseCase: GetAllFoldersUseCase,
    private val addFolderUseCase: AddFolderUseCase,
    private val updateFolderNameUseCase: UpdateFolderNameUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BookmarkState())
    val state = _state.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun onAction(action: BookmarkAction) {
        when (action) {
            BookmarkAction.OnLoadCurrentUser -> getUserInfo()

            is BookmarkAction.OnAddNewFolder -> {
                addFolder(action.name)
                getAllFolders()
            }

            is BookmarkAction.OnDeleteFolder -> {
                deleteFolder(action.folderId)
                getAllFolders()
            }

            BookmarkAction.OnLoadAllFolders -> getAllFolders()

            is BookmarkAction.OnLoadWordsByFolder -> {

            }

            is BookmarkAction.OnUpdateFolderName -> {
                updateFolderName(action.folderId, action.name)
                getAllFolders()
            }

            BookmarkAction.OnClearFolders -> {
                _state.update {
                    it.copy(folders = listOf())
                }
            }
        }
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            getUserInfoUseCase().collect { user ->
                _userInfo.value = user
            }
        }
    }

    private fun getAllFolders() {
        viewModelScope.launch {
            getAllFoldersUseCase().collect { value ->
                _state.update {
                    it.copy(
                        folders = value
                    )
                }
            }
        }
    }

    private fun addFolder(name: String) {
        viewModelScope.launch {
            addFolderUseCase(name)
        }
    }

    private fun updateFolderName(id: String, name: String) {
        viewModelScope.launch {
            updateFolderNameUseCase(id, name)
        }
    }

    private fun deleteFolder(id: String) {
        viewModelScope.launch {
            deleteFolderUseCase(id)
        }
    }
}

