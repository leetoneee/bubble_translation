package com.bteamcoding.bubbletranslation.feature_auth.data.repository

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.api.UserApiService
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponseWrapper
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignInRequest
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.SignUpRequest
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.UserUpdateRequest
import com.bteamcoding.bubbletranslation.feature_auth.domain.repository.AuthRepository
import com.bteamcoding.bubbletranslation.feature_auth.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApiService
) : UserRepository {
    override suspend fun updateUser(id: Long, username: String): ApiResponse<AuthResponseWrapper> {
        val dto = UserUpdateRequest(username = username)
        return api.updateUser(id, dto)
    }

    override suspend fun deleteUser(id: Long): ApiResponse<String> {
        return api.deleteUser(id)
    }
}