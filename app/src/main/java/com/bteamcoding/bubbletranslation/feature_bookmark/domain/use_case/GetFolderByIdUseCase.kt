package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFolderByIdUseCase @Inject constructor(private val repo: BookmarkRepository) {
    operator fun invoke(id: String) : Flow<Folder>  {
        return repo.getFolderById(id)
    }
}