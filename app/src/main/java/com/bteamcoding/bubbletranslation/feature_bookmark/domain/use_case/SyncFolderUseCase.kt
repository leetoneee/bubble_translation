package com.bteamcoding.bubbletranslation.feature_bookmark.domain.use_case

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderDto
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.FolderRepository
import javax.inject.Inject

class SyncFolderUseCase @Inject constructor(private val repo: FolderRepository) {
    suspend operator fun invoke(
        lastSyncTime: Long,
        folders: List<FolderDto>,
        userId: Long
    ): ApiResponse<FolderSyncResponse> {
        val dto = FolderSyncRequest(lastSyncTime, folders)
        return repo.syncFolder(dto, userId)
    }
}