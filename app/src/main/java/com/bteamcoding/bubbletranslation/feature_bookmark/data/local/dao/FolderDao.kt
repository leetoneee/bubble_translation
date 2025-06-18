package com.bteamcoding.bubbletranslation.feature_bookmark.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders WHERE deleted = 0 ORDER BY updatedAt DESC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id = :id")
    fun getFolderById(id: String): Flow<FolderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Query("UPDATE folders SET name = :name, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFolderName(id: String, name: String, updatedAt: Long)

    @Query("UPDATE folders SET deleted = 1, updatedAt = :time WHERE id = :id")
    suspend fun softDeleteFolder(id: String, time: Long)

    @Query("SELECT COUNT(*) FROM folders WHERE name = :name AND deleted = 0")
    suspend fun countActiveFoldersWithName(name: String): Int
}