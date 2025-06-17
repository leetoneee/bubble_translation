package com.bteamcoding.bubbletranslation.feature_bookmark.data.repository

import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.dao.FolderDao
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.dao.WordDao
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity.toDomain
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.toEntity
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val folderDao: FolderDao,
    private val wordDao: WordDao
) : BookmarkRepository {
    override fun getAllFolders(): Flow<List<Folder>> {
        return folderDao.getAllFolders().map { flowList ->
            flowList.map { entityList ->
                entityList.toDomain()
            }
        }
    }

    override fun getFolderById(id: String): Flow<Folder> {
        return folderDao.getFolderById(id)
            .map { it.toDomain() }
    }

    override suspend fun insertFolder(folder: Folder) {
        folderDao.insertFolder(folder.toEntity())
    }

    override suspend fun deleteFolder(id: String) {
        folderDao.softDeleteFolder(id, System.currentTimeMillis())
    }

    override suspend fun updateFolderName(id: String, name: String) {
        val now = System.currentTimeMillis()
        folderDao.updateFolderName(id, name, now)
    }

    override suspend fun countActiveFoldersWithName(name: String): Int {
        return folderDao.countActiveFoldersWithName(name)
    }

    override fun getWordsByFolder(folderId: String): Flow<List<Word>> {
        return wordDao.getWordsByFolder(folderId).map { flowList ->
            flowList.map { entityList ->
                entityList.toDomain()
            }
        }
    }

    override suspend fun insertWord(word: Word) {
        wordDao.insertWord(word.toEntity())
    }

    override suspend fun deleteWord(id: String) {
        wordDao.softDeleteWord(id, System.currentTimeMillis())
    }

    override suspend fun deleteWordsByFolder(id: String) {
        wordDao.softDeleteWordsByFolder(id, System.currentTimeMillis())
    }
}