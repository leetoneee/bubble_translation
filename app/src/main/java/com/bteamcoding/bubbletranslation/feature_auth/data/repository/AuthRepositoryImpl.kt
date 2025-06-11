package com.bteamcoding.bubbletranslation.feature_auth.data.repository

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.api.AuthApiService
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignInRequest
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignUpRequest
import com.bteamcoding.bubbletranslation.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService
) : AuthRepository {
    override suspend fun signUp(username: String, email: String, password: String): ApiResponse<AuthResponse> {
        val dto = SignUpRequest(username, email, password)
        return api.signUp(dto)
    }

    override suspend fun signIn(email: String, password: String): ApiResponse<AuthResponse> {
        val dto = SignInRequest(email, password)
        return api.signIn(dto)
    }
}