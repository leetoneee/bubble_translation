package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.app.domain.use_case.GetUserInfoUseCase
import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.AddFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.DeleteFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.DeleteWordUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetAllFoldersUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetWordsByFolderUseCase
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
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val getWordsByFolderUseCase: GetWordsByFolderUseCase,
    private val deleteWordUseCase: DeleteWordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BookmarkState())
    val state = _state.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo = _userInfo.asStateFlow()

    private var allFolders: List<Folder> = listOf()

    fun onAction(action: BookmarkAction) {
        when (action) {
            BookmarkAction.OnLoadCurrentUser -> getUserInfo()

            BookmarkAction.OnAddNewFolder -> addFolder(_state.value.folderName)

            BookmarkAction.OnLoadAllFolders -> {
                _state.update {
                    it.copy(currentFolder = null, searchQuery = "", words = listOf())
                }
                getAllFolders()
            }

            is BookmarkAction.OnLoadWordsByFolder -> {
                getAllWordsByFolder(action.folderId)
            }

            BookmarkAction.OnClearFolders -> {
                _state.update {
                    it.copy(folders = listOf())
                }
            }

            is BookmarkAction.OnQueryChanged -> {
                _state.update { it.copy(searchQuery = action.query) }
                if (action.query != "") {
                    val filterFolders = searchFolder(action.query, allFolders)
                    _state.update {
                        it.copy(
                            folders = filterFolders
                        )
                    }
                } else {
                    getAllFolders()
                }
            }

            is BookmarkAction.OnFolderClick -> {
                _state.update { it.copy(currentFolder = action.folder) }
            }

            BookmarkAction.OnShowAddFolder -> {
                _state.update {
                    it.copy(
                        showAddFolderDialog = true,
                    )
                }
            }

            BookmarkAction.OnHideAddFolder -> {
                _state.update {
                    it.copy(
                        showAddFolderDialog = false,
                        folderName = ""
                    )
                }
            }

            is BookmarkAction.OnFolderNameChanged -> {
                _state.update { it.copy(folderName = action.name) }
            }

            BookmarkAction.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }

            BookmarkAction.OnUpdateFolderName -> _state.value.tempFolder?.let {
                updateFolderName(
                    it.id,
                    _state.value.folderName
                )
            }

            BookmarkAction.OnHideEditFolder -> {
                _state.update {
                    it.copy(
                        showEditFolderDialog = false,
                        tempFolder = null,
                    )
                }
            }

            is BookmarkAction.OnShowEditFolder -> {
                _state.update {
                    it.copy(
                        showEditFolderDialog = true,
                        tempFolder = action.folder,
                    )
                }
            }

            is BookmarkAction.OnDeleteFolder -> _state.value.tempFolder?.let { deleteFolder(it.id) }

            BookmarkAction.OnHideConfirm -> {
                _state.update {
                    it.copy(
                        showConfirmDialog = false,
                        tempFolder = null
                    )
                }
            }

            is BookmarkAction.OnShowConfirm -> {
                _state.update {
                    it.copy(
                        showConfirmDialog = true,
                        tempFolder = action.folder
                    )
                }
            }

            is BookmarkAction.OnSetTempFolder -> {
                _state.update {
                    it.copy(
                        tempFolder = action.folder,
                    )
                }
            }

            BookmarkAction.OnDeleteWord -> _state.value.tempWord?.let { deleteWord(it.id) }

            BookmarkAction.OnHideConfirmDeleteWord -> {
                _state.update {
                    it.copy(
                        showConfirmDeleteWordDialog = false,
                        tempWord = null
                    )
                }
            }

            is BookmarkAction.OnShowConfirmDeleteWord -> {
                _state.update {
                    it.copy(
                        showConfirmDeleteWordDialog = true,
                        tempWord = action.word
                    )
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
                allFolders = value
            }
        }
    }

    private fun getAllWordsByFolder(folderId: String) {
        viewModelScope.launch {
            getWordsByFolderUseCase(folderId = folderId).collect { value ->
                Log.i("words", value.toString())
                _state.update {
                    it.copy(
                        words = value
                    )
                }
            }
        }
    }

    private fun addFolder(name: String) {
        viewModelScope.launch {
            runCatching {
                addFolderUseCase(name)
            }.onSuccess {
                _state.update {
                    it.copy(
                        showAddFolderDialog = false,
                        folderName = ""
                    )
                }
                getAllFolders()
            }.onFailure { t ->
                _state.update { it.copy(errorMessage = t.message) }
            }
        }
    }

    private fun updateFolderName(id: String, name: String) {
        viewModelScope.launch {
            runCatching {
                updateFolderNameUseCase(id, name)
            }.onSuccess {
                _state.update {
                    it.copy(
                        showEditFolderDialog = false,
                        folderName = "",
                        tempFolder = null
                    )
                }
                getAllFolders()
            }.onFailure { t ->
                _state.update { it.copy(errorMessage = t.message) }
            }
        }
    }

    private fun deleteWord(id: String) {
        viewModelScope.launch {
            runCatching {
                deleteWordUseCase(id = id)
            }.onSuccess {
                _state.update {
                    it.copy(
                        showConfirmDeleteWordDialog = false,
                        tempFolder = null
                    )
                }
            }.onFailure { t ->
                _state.update { it.copy(errorMessage = t.message) }
            }
        }
    }

    private fun deleteFolder(id: String) {
        viewModelScope.launch {
            runCatching {
                deleteFolderUseCase(id)
            }.onSuccess {
                _state.update {
                    it.copy(
                        showConfirmDialog = false,
                        tempFolder = null
                    )
                }
                getAllFolders()
            }.onFailure { t ->
                _state.update { it.copy(errorMessage = t.message) }
            }
        }
    }

    private fun searchFolder(query: String, folders: List<Folder>): List<Folder> {
        val normalizedQuery = query.trim().lowercase()

        if (normalizedQuery.isBlank()) {
            // Trả lại toàn bộ nếu không có query
            return folders
        }

        val filtered = allFolders.filter { folder ->
            folder.name.lowercase().contains(normalizedQuery)
        }

        return filtered
    }
}

