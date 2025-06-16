package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import javax.inject.Inject

class DeleteFolderUseCase @Inject constructor(
    private val repo: BookmarkRepository
) {
    suspend operator fun invoke(id: String) {
        repo.deleteFolder(id)
        repo.deleteWordsByFolder(id)
    }
}