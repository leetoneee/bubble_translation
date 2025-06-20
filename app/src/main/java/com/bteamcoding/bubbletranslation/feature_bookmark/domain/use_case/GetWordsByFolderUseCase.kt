package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordsByFolderUseCase @Inject constructor(private val repo: BookmarkRepository) {
    operator fun invoke(folderId: String): Flow<List<Word>> {
        return repo.getWordsByFolder(folderId)
    }
}