package com.bteamcoding.bubbletranslation.feature_auth.data.network

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponseWrapper
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignInRequest
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignUpRequest

interface AuthApi {
    suspend fun signIn(request: SignInRequest): ApiResponse<AuthResponse>
    suspend fun signUp(request: SignUpRequest): ApiResponse<AuthResponseWrapper>
}