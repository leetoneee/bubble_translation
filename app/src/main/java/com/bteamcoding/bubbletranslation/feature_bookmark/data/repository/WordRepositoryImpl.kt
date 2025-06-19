package com.bteamcoding.bubbletranslation.feature_bookmark.data.repository

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.api.WordApiService
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository.WordRepository
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val api: WordApiService
) : WordRepository {
    override suspend fun syncWord(
        request: WordSyncRequest,
        userId: Long
    ): ApiResponse<WordSyncResponse> {
        return api.syncWord(request, userId)
    }

}