package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.core.utils.callApiForTranslation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class DictionaryViewModel : ViewModel() {
    private val _state = MutableStateFlow(DictionaryScreenState())
    val state = _state.asStateFlow()

    fun onAction(action: DictionaryAction) {
        when (action) {
            is DictionaryAction.Search -> {
                _state.value = _state.value.copy(
                    searchQuery = action.query,
                    isLoading = true,
                    error = null
                )
                searchWord(action.query)
            }
            is DictionaryAction.ClearSearch -> {
                _state.value = _state.value.copy(
                    searchQuery = "",
                    definitions = emptyList(),
                    error = null
                )
            }
            is DictionaryAction.UpdateQuery -> {
                _state.value = _state.value.copy(
                    searchQuery = action.query
                )
            }
        }
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

    private fun parseDictionaryEntries(json: String?): List<DictionaryEntry> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                DictionaryEntry(
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
        } catch (e: Exception) {
            emptyList()
        }
    }
}
