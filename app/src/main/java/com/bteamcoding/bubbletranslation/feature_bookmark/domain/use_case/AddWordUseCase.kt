package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import java.util.UUID
import javax.inject.Inject

class AddWordUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(folderId: String, text: String) {
        val normalizeWord = text.trim().replaceFirstChar { it.uppercaseChar() }

        val isDuplicate = repo.countActiveWord(normalizeWord) > 0
        if (isDuplicate) throw Exception("Từ \"$normalizeWord\" đã tồn tại trong hệ thống.")


        val word = Word(
            id = UUID.randomUUID().toString(),
            word = normalizeWord,
            updatedAt = System.currentTimeMillis(),
            folderId = folderId,
            deleted = false
        )
        repo.insertWord(word)
    }
}