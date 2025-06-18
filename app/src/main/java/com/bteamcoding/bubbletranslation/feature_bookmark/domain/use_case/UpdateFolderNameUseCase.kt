package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import javax.inject.Inject

class UpdateFolderNameUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(id: String, newName: String) {
        val exists = repo.countActiveFoldersWithName(newName) > 0
        if (exists) throw Exception("Folder with name \"$newName\" already exists.")

        if (newName.isBlank()) throw Exception("Folder name cannot be empty.")

        repo.updateFolderName(id, newName)
    }
}