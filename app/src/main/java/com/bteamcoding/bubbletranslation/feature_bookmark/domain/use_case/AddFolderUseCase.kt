package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import java.util.UUID
import javax.inject.Inject

class AddFolderUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(name: String) {

        val normalizeName = name.trim().replaceFirstChar { it.uppercaseChar() }

        val exists = repo.countActiveFoldersWithName(normalizeName) > 0
        if (exists) throw Exception("Folder with name \"$normalizeName\" already exists.")

        if (normalizeName.isBlank()) throw Exception("Folder name cannot be empty.")


        val folder = Folder(
            id = UUID.randomUUID().toString(),
            name = normalizeName,
            updatedAt = System.currentTimeMillis(),
            deleted = false
        )
        repo.insertFolder(folder)
    }
}