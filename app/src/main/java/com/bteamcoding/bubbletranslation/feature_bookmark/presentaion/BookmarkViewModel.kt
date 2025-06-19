package com.bteamcoding.bubbletranslation.feature_bookmark.presentaion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.app.domain.use_case.GetLastSyncTimeUseCase
import com.bteamcoding.bubbletranslation.app.domain.use_case.GetUserInfoUseCase
import com.bteamcoding.bubbletranslation.app.domain.use_case.SaveLastSyncTimeUseCase
import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.AddFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.AddServerFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.AddServerWordUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.DeleteFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.DeleteWordUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetAllFoldersIncludingDeletedUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetAllFoldersUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetAllWordsIncludingDeletedUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetAllWordsUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetWordsByFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.SyncFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.SyncWordUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.UpdateFolderNameUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.UpdateFolderUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getAllFoldersUseCase: GetAllFoldersUseCase,
    private val getAllFoldersIncludingDeletedUseCase: GetAllFoldersIncludingDeletedUseCase,
    private val getAllWordsIncludingDeletedUseCase: GetAllWordsIncludingDeletedUseCase,
    private val getAllWordsUseCase: GetAllWordsUseCase,
    private val addFolderUseCase: AddFolderUseCase,
    private val updateFolderNameUseCase: UpdateFolderNameUseCase,
    private val updateFolderUserIdUseCase: UpdateFolderUserIdUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val getWordsByFolderUseCase: GetWordsByFolderUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
    private val getLastSyncTimeUseCase: GetLastSyncTimeUseCase,
    private val saveLastSyncTimeUseCase: SaveLastSyncTimeUseCase,
    private val syncFolderUseCase: SyncFolderUseCase,
    private val syncWordUseCase: SyncWordUseCase,
    private val addServerWordUseCase: AddServerWordUseCase,
    private val addServerFolderUseCase: AddServerFolderUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BookmarkState())
    val state = _state.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long>(0)
    val lastSyncTime = _lastSyncTime.asStateFlow()

    fun onAction(action: BookmarkAction) {
        when (action) {
            BookmarkAction.OnLoadCurrentUser -> {
                getUserInfo()
                getLastSyncTime()
                getAllWords()
            }

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
                    if (_state.value.currentFolder != null) {
                        val filterWords = searchWord(action.query, _state.value.allWords)
                        _state.update {
                            it.copy(
                                words = filterWords
                            )
                        }
                    } else {
                        val filterFolders = searchFolder(action.query, _state.value.allFolders)
                        _state.update {
                            it.copy(
                                folders = filterFolders
                            )
                        }
                    }
                } else {
                    if (_state.value.currentFolder != null) {
                        getAllWordsByFolder(_state.value.currentFolder!!.id)
                    } else {
                        getAllFolders()
                    }
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

            BookmarkAction.OnSync -> {
                if (_userInfo.value != null && _userInfo.value!!.id != 0L) {
                    viewModelScope.launch {
                        _state.update { it.copy(isLoading = true) }
                        runCatching {
                            val userId = _userInfo.value!!.id
                            val lastSync = _lastSyncTime.value

                            // Bước 1: Cập nhật user ID cho các folder cục bộ.
                            updateFolderUser(userId)

                            // Bước 2: Lấy dữ liệu mới nhất từ DB một cách tuần tự.
                            val currentFolders = getAllFoldersUseCase().first()
                            val currentWords = getAllWordsUseCase().first()

                            _state.update { it.copy(allFolders = currentFolders, allLocalWords = currentWords) }

                            // Bước 3: Đồng bộ folders và words lên server.
                            val foldersToSync = filterFoldersToSync(currentFolders,lastSync)
                            val syncedFolders = syncFolders(userId, foldersToSync, lastSync)

                            val wordsToSync = filterWordsToSync(currentWords, lastSync)
                            val syncedWords = syncWords(userId, wordsToSync, lastSync)

                            // Bước 4: Lưu dữ liệu mới từ server vào DB (nếu có).
                            if (syncedFolders.isNotEmpty()) {
                                addServerFolders(syncedFolders)
                            }
                            if (syncedWords.isNotEmpty()) {
                                addServerWords(syncedWords)
                            }

                            // Bước 5: Nếu mọi thứ thành công, lưu lại thời gian đồng bộ.
                            saveLastSyncTime(System.currentTimeMillis())

                            // Bước 6: Tải lại toàn bộ dữ liệu từ DB để làm mới UI.
                            getAllFolders()
                            getAllWords()

                        }.onFailure { t ->
                            _state.update {
                                it.copy(errorMessage = t.message ?: "Đã có lỗi đồng bộ xảy ra")
                            }
                        }.also {
                            _state.update { it.copy(isLoading = false) } // Tắt loading dù thành công hay thất bại
                        }
                    }
                } else {
                    _state.update {
                        it.copy(errorMessage = "Vui lòng đăng nhập để sử dụng chức năng này")
                    }
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

    private fun filterFoldersToSync(folders: List<Folder>, lastSyncTime: Long): List<Folder> {
        return folders.filter { folder ->
            folder.updatedAt > lastSyncTime
        }
    }

    private fun filterWordsToSync(words: List<Word>, lastSyncTime: Long): List<Word> {
        return words.filter { word ->
            word.updatedAt > lastSyncTime
        }
    }


    private fun getLastSyncTime() {
        viewModelScope.launch {
            getLastSyncTimeUseCase().collect { time ->
                _lastSyncTime.value = time
            }
        }
    }

    private suspend fun saveLastSyncTime(time: Long) {
        runCatching {
            saveLastSyncTimeUseCase(time)
        }.onSuccess {
            _lastSyncTime.value = time
        }.onFailure { t ->
            throw t
        }
    }

    private suspend fun syncWords(userId: Long, words: List<Word>, lastSyncTime: Long): List<Word> {
        val response = run {
            syncWordUseCase(userId = userId, words = words, lastSyncTime = lastSyncTime)
        }

        if (response.code == 200) {
            return response.result?.synced ?: emptyList()
        } else {
            throw Exception("Sync words failed with code ${response.code}")
        }
    }

    private suspend fun syncFolders(
        userId: Long,
        folders: List<Folder>,
        lastSyncTime: Long
    ): List<Folder> {
        val response = run {
            syncFolderUseCase(userId = userId, folders = folders, lastSyncTime = lastSyncTime)
        }

        if (response.code == 200) {
            return response.result?.synced ?: emptyList()
        } else {
            // Ném lỗi với thông điệp từ server nếu có
//            _state.update { it.copy(errorMessage = response.message) }
            throw Exception("Sync folders failed with message: ${response.message}")
        }
    }

    private fun getAllFolders() {
        viewModelScope.launch {
            getAllFoldersUseCase().collect { value ->
                _state.update {
                    it.copy(
                        folders = value,
                        allFolders = value
                    )
                }
            }
        }
    }

    private fun getAllWords() {
        viewModelScope.launch {
            getAllWordsUseCase().collect { value ->
                _state.update {
                    it.copy(
                        allLocalWords = value
                    )
                }
            }
        }
    }

    private fun getAllWordsByFolder(folderId: String) {
        viewModelScope.launch {
            getWordsByFolderUseCase(folderId = folderId).collect { value ->
                Log.i("words", value.toString())
                _state.update {
                    it.copy(
                        words = value,
                        allWords = value
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

    private suspend fun addServerFolders(folders: List<Folder>) {
        runCatching {
            addServerFolderUseCase(folders)
        }.onFailure { t ->
            _state.update { it.copy(errorMessage = t.message) }
        }
    }

    private suspend fun addServerWords(words: List<Word>) {
        runCatching {
            addServerWordUseCase(words)
        }.onFailure { t ->
            _state.update { it.copy(errorMessage = t.message) }
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

    private suspend fun updateFolderUser(userId: Long) {
        runCatching {
            updateFolderUserIdUseCase(userId)
        }.onFailure { t ->
            _state.update { it.copy(errorMessage = t.message) }
            throw t
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

        val filtered = folders.filter { folder ->
            folder.name.lowercase().contains(normalizedQuery)
        }

        return filtered
    }

    private fun searchWord(query: String, words: List<Word>): List<Word> {
        val normalizedQuery = query.trim().lowercase()

        if (normalizedQuery.isBlank()) {
            // Trả lại toàn bộ nếu không có query
            return words
        }

        val filtered = words.filter { word ->
            word.word.lowercase().contains(normalizedQuery)
        }

        return filtered
    }
}
