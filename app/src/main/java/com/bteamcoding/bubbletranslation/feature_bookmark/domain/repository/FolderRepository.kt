package com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncResponse

interface FolderRepository {
    suspend fun syncFolder(
        request: FolderSyncRequest,
        userId: Long
    ): ApiResponse<FolderSyncResponse>
}