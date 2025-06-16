package com.bteamcoding.bubbletranslation.feature_auth.domain.repository

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponseWrapper
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.UserUpdateRequest

interface UserRepository  {
    suspend fun updateUser(id: Long, username: String): ApiResponse<AuthResponseWrapper>
    suspend fun deleteUser(id: Long): ApiResponse<String>
}