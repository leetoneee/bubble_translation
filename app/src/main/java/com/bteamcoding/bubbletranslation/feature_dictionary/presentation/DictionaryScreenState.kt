package com.bteamcoding.bubbletranslation.feature_dictionary.presentation

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder

data class Meaning(
    val meaning: String,
    val vietnamese: String,
    val example_sentence: String,
    val vietnamese_translation: String
)

data class DictionaryEntry(
    val english: String,
    val phonetic: String,
    val part_of_speech: String,
    val meanings: List<Meaning>
)

data class DictionaryScreenState(
    val searchQuery: String = "",
    val definitions: List<DictionaryEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorMessage: String? = null,
    val folders: List<Folder> = listOf(),
    val folderName: String = "",
    val showAddFolderDialog: Boolean = false,
    val showAddWordDialog: Boolean = false,
    val isSavedWord: Boolean = false
)
