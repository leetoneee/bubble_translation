package com.bteamcoding.bubbletranslation.feature_auth.domain.use_case

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponse
import com.bteamcoding.bubbletranslation.feature_auth.domain.repository.AuthRepository
import com.bteamcoding.bubbletranslation.feature_auth.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(id: Long): ApiResponse<String> =
        repo.deleteUser(id)
}