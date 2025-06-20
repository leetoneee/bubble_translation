package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.core.utils.ContextProvider
import com.bteamcoding.bubbletranslation.core.utils.callApiForTranslation
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.AddFolderUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.AddWordUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetAllFoldersUseCase
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case.GetWordByNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class DictionaryViewModel2 : ViewModel() {
    private val _state = MutableStateFlow(DictionaryScreenState())
    val state = _state.asStateFlow()
//    private val getAllFoldersUseCase = GetAllFoldersUseCase(
//        repo = TODO()
//    )
//    private val addFolderUseCase = AddFolderUseCase(
//        repo = TODO()
//    )
//    private val addWordUseCase = AddWordUseCase(
//        repo = TODO()
//    )
//    private val getWordByNameUseCase = GetWordByNameUseCase(
//        repo = TODO()
//    )

    fun onAction(action: DictionaryAction) {
        when (action) {
            is DictionaryAction.Search -> {
                _state.value = _state.value.copy(
                    searchQuery = action.query,
                    definitions = emptyList(), // Clear previous definitions
                    isLoading = true,
                    error = null
                )
                searchWord(action.query)
                getWord(action.query)
            }

            is DictionaryAction.ClearSearch -> {
                _state.value = _state.value.copy(
                    searchQuery = "",
                    isSavedWord = false,
                    // definitions = emptyList(),
                    error = null
                )
            }

            is DictionaryAction.UpdateQuery -> {
                _state.value = _state.value.copy(
                    searchQuery = action.query
                )
            }

            DictionaryAction.OnAddNewFolder -> addFolder(_state.value.folderName)
            DictionaryAction.OnHideAddFolder -> {
                _state.update {
                    it.copy(
                        showAddFolderDialog = false,
                        folderName = ""
                    )
                }
            }

            DictionaryAction.OnShowAddFolder -> {
                _state.update {
                    it.copy(
                        showAddFolderDialog = true,
                    )
                }
                getAllFolders()
            }

            DictionaryAction.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }

            is DictionaryAction.OnFolderNameChanged -> {
                _state.update { it.copy(folderName = action.name) }
            }

            is DictionaryAction.OnAddNewWord -> addWord(_state.value.searchQuery, action.folder.id)

            DictionaryAction.OnHideAddWord -> {
                _state.update {
                    it.copy(
                        showAddWordDialog = false,
                    )
                }
            }

            DictionaryAction.OnShowAddWord -> {
                _state.update {
                    it.copy(
                        showAddWordDialog = true,
                    )
                }
            }

            DictionaryAction.OnLoadAllFolders -> getAllFolders()
        }
    }

    private fun addWord(word: String, id: String) {
//        viewModelScope.launch {
//            runCatching {
//                addWordUseCase(folderId = id, text = word)
//            }.onSuccess {
//                _state.update {
//                    it.copy(
//                        showAddWordDialog = false,
//                    )
//                }
//            }.onFailure { t ->
//                _state.update { it.copy(errorMessage = t.message) }
//            }
//        }
    }

    private fun addFolder(name: String) {
//        viewModelScope.launch {
//            runCatching {
//                addFolderUseCase(name)
//            }.onSuccess {
//                _state.update {
//                    it.copy(
//                        showAddFolderDialog = false,
//                        folderName = ""
//                    )
//                }
//                getAllFolders()
//            }.onFailure { t ->
//                _state.update { it.copy(errorMessage = t.message) }
//            }
//        }
    }

    private fun getAllFolders() {
//        viewModelScope.launch {
//            getAllFoldersUseCase().collect { value ->
//                _state.update {
//                    it.copy(
//                        folders = value
//                    )
//                }
//            }
//        }
    }

    private fun getWord(query: String) {
//        viewModelScope.launch {
//            runCatching {
//                getWordByNameUseCase(query)
//            }.onSuccess { value ->
//                Log.i("word", value.toString())
//                _state.update {
//                    it.copy(
//                        isSavedWord = value
//                    )
//                }
//            }.onFailure {
//                _state.update {
//                    it.copy(
//                        isSavedWord = false
//                    )
//                }
//            }
//        }
    }

    private fun searchWord(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _state.value = _state.value.copy(
                    definitions = emptyList(),
                    isLoading = false,
                    error = "Please enter a word."
                )
            } else {
                if (!isNetworkAvailable()) {
                    // Hiển thị thông báo cho người dùng cần kết nối mạng
                    _state.value = _state.value.copy(
                        definitions = emptyList(),
                        isLoading = false,
                        error = "Please connect to the internet."
                    )
                } else {
                    val text = withContext(Dispatchers.IO) { callApiForTranslation(query) }
                    // val text = "[{\"english\":\"watch\",\"phonetic\":\"\\/wɑːtʃ\\/\",\"part_of_speech\":\"verb\",\"meanings\":[{\"meaning\":\"To look at (something or someone) for an extended period of time.\",\"vietnamese\":\"Xem, quan sát\",\"example_sentence\":\"I like to watch the sunset every evening.\",\"vietnamese_translation\":\"Tôi thích xem hoàng hôn mỗi buổi tối.\"},{\"meaning\":\"To observe attentively so as to guard or protect something or someone.\",\"vietnamese\":\"Canh giữ, trông chừng\",\"example_sentence\":\"Please watch my bag while I go to the restroom.\",\"vietnamese_translation\":\"Vui lòng trông chừng túi của tôi trong khi tôi đi vệ sinh.\"},{\"meaning\":\"To be careful or wary; to be on one's guard.\",\"vietnamese\":\"Cẩn thận, dè chừng\",\"example_sentence\":\"Watch out for pickpockets in crowded areas.\",\"vietnamese_translation\":\"Hãy coi chừng móc túi ở những khu vực đông người.\"}]},{\"english\":\"watch\",\"phonetic\":\"\\/wɑːtʃ\\/\",\"part_of_speech\":\"noun\",\"meanings\":[{\"meaning\":\"A small timepiece worn typically on a strap on one's wrist.\",\"vietnamese\":\"Đồng hồ đeo tay\",\"example_sentence\":\"He received a new watch for his birthday.\",\"vietnamese_translation\":\"Anh ấy đã nhận được một chiếc đồng hồ mới cho ngày sinh nhật của mình.\"},{\"meaning\":\"The act of keeping guard; surveillance.\",\"vietnamese\":\"Sự canh gác, sự giám sát\",\"example_sentence\":\"The security guard kept a close watch on the building.\",\"vietnamese_translation\":\"Nhân viên bảo vệ canh gác tòa nhà cẩn thận.\"},{\"meaning\":\"A period of time, typically four hours, during which some of a ship's crew are on duty.\",\"vietnamese\":\"Ca trực (trên tàu)\",\"example_sentence\":\"The captain assigned him to the night watch.\",\"vietnamese_translation\":\"Thuyền trưởng đã giao anh ấy ca trực đêm.\"}]}]"
                    val entries = parseDictionaryEntries(text)
                    _state.value = _state.value.copy(
                        definitions = entries,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

//    private fun parseDictionaryEntries(json: String?): List<DictionaryEntry> {
//        if (json.isNullOrBlank()) return emptyList()
//        return try {
//            val arr = JSONArray(json)
//            List(arr.length()) { i ->
//                val obj = arr.getJSONObject(i)
//                DictionaryEntry(
//                    english = obj.getString("english"),
//                    phonetic = obj.getString("phonetic"),
//                    part_of_speech = obj.getString("part_of_speech"),
//                    meanings = obj.getJSONArray("meanings").let { meaningsArr ->
//                        List(meaningsArr.length()) { j ->
//                            val m = meaningsArr.getJSONObject(j)
//                            Meaning(
//                                meaning = m.getString("meaning"),
//                                vietnamese = m.getString("vietnamese"),
//                                example_sentence = m.getString("example_sentence"),
//                                vietnamese_translation = m.getString("vietnamese_translation")
//                            )
//                        }
//                    }
//                )
//            }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }

    private fun parseDictionaryEntries(json: String?): List<DictionaryEntry> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val entries = mutableListOf<DictionaryEntry>()

            val jsonElement = json.trim()

            // Kiểm tra xem là JSONArray hay JSONObject
            if (jsonElement.startsWith("[")) {
                // Nếu là JSONArray
                val arr = JSONArray(jsonElement)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    entries.add(parseEntry(obj))
                }
            } else {
                // Nếu là JSONObject đơn lẻ
                val obj = JSONObject(jsonElement)
                entries.add(parseEntry(obj))
            }

            entries
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Hàm phụ để parse một DictionaryEntry từ JSONObject
    private fun parseEntry(obj: JSONObject): DictionaryEntry {
        return DictionaryEntry(
            english = obj.getString("english"),
            phonetic = obj.getString("phonetic"),
            part_of_speech = obj.getString("part_of_speech"),
            meanings = obj.getJSONArray("meanings").let { meaningsArr ->
                List(meaningsArr.length()) { j ->
                    val m = meaningsArr.getJSONObject(j)
                    Meaning(
                        meaning = m.getString("meaning"),
                        vietnamese = m.getString("vietnamese"),
                        example_sentence = m.getString("example_sentence"),
                        vietnamese_translation = m.getString("vietnamese_translation")
                    )
                }
            }
        )
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = ContextProvider.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

}