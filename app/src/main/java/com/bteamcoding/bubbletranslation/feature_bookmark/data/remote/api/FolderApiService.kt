package com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.api

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.network.AuthApi
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponseWrapper
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignInRequest
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignUpRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.network.FolderApi
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncRequest
import com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto.FolderSyncResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class FolderApiService @Inject constructor(
    private val client: HttpClient
) : FolderApi {

    private val baseUrl = "http://127.0.0.1:8080/api"

    override suspend fun syncFolder(
        request: FolderSyncRequest,
        userId: Long
    ): ApiResponse<FolderSyncResponse> =
        client.post("$baseUrl/folders/sync?userId=$userId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}