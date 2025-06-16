package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import java.util.UUID
import javax.inject.Inject

class AddWordUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(folderId: String, text: String) {
        val word = Word(
            id = UUID.randomUUID().toString(),
            word = text,
            updatedAt = System.currentTimeMillis(),
            folderId = folderId,
            deleted = false
        )
        repo.insertWord(word)
    }
}