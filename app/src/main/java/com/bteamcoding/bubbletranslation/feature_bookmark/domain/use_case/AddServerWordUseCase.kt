package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import java.util.UUID
import javax.inject.Inject

class AddServerWordUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(words: List<Word>) {
        for (word in words) {
            repo.insertWord(word)
        }
    }
}