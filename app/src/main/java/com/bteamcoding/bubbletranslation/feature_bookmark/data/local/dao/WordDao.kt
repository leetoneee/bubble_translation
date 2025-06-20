package com.bteamcoding.bubbletranslation.feature_bookmark.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity.FolderEntity
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE deleted = 0 ORDER BY updatedAt DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE deleted = 1 AND updatedAt > :lastSync ORDER BY updatedAt DESC")
    fun getAllWordsIncludingDeleted(lastSync: Long): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE folderId = :folderId AND deleted = 0")
    fun getWordsByFolder(folderId: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordById(id: String): Flow<WordEntity>

    @Query("SELECT * FROM words WHERE word = :name AND deleted = 0")
    fun getWordByName(name: String): Flow<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Query("UPDATE words SET deleted = 1, updatedAt = :time WHERE id = :id")
    suspend fun softDeleteWord(id: String, time: Long)

    @Query("UPDATE words SET deleted = 1, updatedAt = :time WHERE folderId = :folderId")
    suspend fun softDeleteWordsByFolder(folderId: String, time: Long)

    @Query("SELECT COUNT(*) FROM words WHERE word = :word AND deleted = 0")
    suspend fun countActiveWord(word: String): Int
}