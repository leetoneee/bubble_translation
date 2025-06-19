package com.bteamcoding.bubbletranslation.feature_bookmark.data.repository

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.api.FolderApiService
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.FolderRepository
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val api: FolderApiService
) : FolderRepository{
    override suspend fun syncFolder(
        request: FolderSyncRequest,
        userId: Long
    ): ApiResponse<FolderSyncResponse> {
        return api.syncFolder(request, userId)
    }
}