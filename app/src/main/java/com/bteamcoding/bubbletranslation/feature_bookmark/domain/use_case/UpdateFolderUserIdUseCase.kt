package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import javax.inject.Inject

class UpdateFolderUserIdUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(userId: Long) {
        if (userId == 0L) throw Exception("User Id cannot be zero.")

        repo.updateFolderUserId(userId)
    }
}