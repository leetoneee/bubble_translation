package com.bteamcoding.bubbletranslation.feature_bookmark.domain.repository

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncResponse

interface WordRepository {
    suspend fun syncWord(
        request: WordSyncRequest,
        userId: Long
    ): ApiResponse<WordSyncResponse>
}