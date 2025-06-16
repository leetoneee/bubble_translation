package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import java.util.UUID
import javax.inject.Inject

class AddFolderUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(name: String) {
        val folder = Folder(
            id = UUID.randomUUID().toString(),
            name = name,
            updatedAt = System.currentTimeMillis(),
            deleted = false
        )
        repo.insertFolder(folder)
    }
}