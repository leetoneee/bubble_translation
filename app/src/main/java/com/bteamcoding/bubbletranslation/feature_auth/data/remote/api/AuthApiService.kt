package com.bteamcoding.bubbletranslation.feature_auth.data.remote.api

import com.bteamcoding.bubbletranslation.app.data.remote.KtorClientProvider
import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.network.AuthApi
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponseWrapper
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignInRequest
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignUpRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthApiService @Inject constructor(
    private val client: HttpClient
) : AuthApi {

    private val baseUrl = "http://127.0.0.1:8080/api"

    override suspend fun signUp(request: SignUpRequest): ApiResponse<AuthResponseWrapper> =
        client.post("$baseUrl/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun signIn(request: SignInRequest): ApiResponse<AuthResponse> =
        client.post("$baseUrl/auth/signin") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}