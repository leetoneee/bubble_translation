package com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getAllFolders(): Flow<List<Folder>>
    fun getAllFoldersIncludingDeleted(): Flow<List<Folder>>
    fun getFolderById(id: String): Flow<Folder>
    suspend fun insertFolder(folder: Folder)
    suspend fun deleteFolder(id: String)
    suspend fun updateFolderName(id: String, name: String)
    suspend fun updateFolderUserId(userId: Long)
    suspend fun countActiveFoldersWithName(name: String) : Int

    fun getWordsByFolder(folderId: String): Flow<List<Word>>
    fun getAllWordsIncludingDeleted(): Flow<List<Word>>
    fun getAllWords(): Flow<List<Word>>
    fun getWordById(id: String): Flow<Word>
    fun getWordByName(name: String): Flow<Word>
    suspend fun insertWord(word: Word)
    suspend fun deleteWord(id: String)
    suspend fun deleteWordsByFolder(id: String)
    suspend fun countActiveWord(word: String) : Int
}