package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import javax.inject.Inject

class UpdateFolderNameUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(id: String, newName: String) {
        repo.updateFolderName(id, newName)
    }
}