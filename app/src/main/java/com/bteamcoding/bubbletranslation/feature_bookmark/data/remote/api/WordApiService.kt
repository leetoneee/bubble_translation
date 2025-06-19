package com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.api

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.network.WordApi
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncResponse
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.WordSyncResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class WordApiService @Inject constructor(
    private val client: HttpClient
) : WordApi {

    private val baseUrl = "http://127.0.0.1:8080/api"

    override suspend fun syncWord(
        request: WordSyncRequest,
        userId: Long
    ): ApiResponse<WordSyncResponse> =
        client.post("$baseUrl/words/sync?userId=$userId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}