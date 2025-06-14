package com.bteamcoding.bubbletranslation.feature_auth.data.network

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponseWrapper
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.UserUpdateRequest

interface UserApi {
    suspend fun updateUser(id: Long, request: UserUpdateRequest): ApiResponse<AuthResponseWrapper>
    suspend fun deleteUser(id: Long): ApiResponse<String>
}