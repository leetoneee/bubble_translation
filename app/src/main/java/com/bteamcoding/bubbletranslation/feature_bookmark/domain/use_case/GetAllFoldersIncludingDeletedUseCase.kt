package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFoldersIncludingDeletedUseCase @Inject constructor(private val repo: BookmarkRepository) {
    operator fun invoke(): Flow<List<Folder>> {
        return repo.getAllFoldersIncludingDeleted()
    }
}