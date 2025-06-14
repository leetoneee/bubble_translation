package com.bteamcoding.bubbletranslation.feature_auth.data.remote.api

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.network.UserApi
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponseWrapper
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.UserUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class UserApiService @Inject constructor(
    private val client: HttpClient
) : UserApi {

    private val baseUrl = "http://127.0.0.1:8080/api"

    override suspend fun updateUser(
        id: Long,
        request: UserUpdateRequest
    ): ApiResponse<AuthResponseWrapper> =
        client.patch("$baseUrl/users/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()


    override suspend fun deleteUser(id: Long): ApiResponse<String> =
        client.delete("$baseUrl/users/$id") {
            contentType(ContentType.Application.Json)
        }.body()
}