package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordByIdUseCase @Inject constructor(private val repo: BookmarkRepository) {
    operator fun invoke(id: String) : Flow<Word> {
        return repo.getWordById(id)
    }
}