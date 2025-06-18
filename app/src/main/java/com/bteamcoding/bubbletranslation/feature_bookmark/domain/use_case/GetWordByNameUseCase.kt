package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordByNameUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(name: String) : Boolean {
        val normalizeWord = name.trim().replaceFirstChar { it.uppercaseChar() }

        val isExist = repo.countActiveWord(normalizeWord) == 1

        return isExist
    }
}