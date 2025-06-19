package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.BookmarkRepository
import java.util.UUID
import javax.inject.Inject

class AddServerFolderUseCase @Inject constructor(private val repo: BookmarkRepository) {
    suspend operator fun invoke(folders: List<Folder>) {
        for (folder in folders) {
            repo.insertFolder(folder)
        }
    }
}