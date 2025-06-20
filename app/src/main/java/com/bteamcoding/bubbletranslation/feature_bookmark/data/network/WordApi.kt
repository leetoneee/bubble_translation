package com.bteamcoding.bubbletranslation.feature_bookmark.data.network

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncResponse

interface WordApi {
    suspend fun syncWord(request: WordSyncRequest, userId: Long): ApiResponse<WordSyncResponse>
}