package com.bteamcoding.bubbletranslation.feature_auth.domain.repository

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponseWrapper

interface AuthRepository {
    suspend fun signUp(username: String, email: String, password: String): ApiResponse<AuthResponseWrapper>
    suspend fun signIn(email: String, password: String): ApiResponse<AuthResponse>
}