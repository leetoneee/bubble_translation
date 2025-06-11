package com.bteamcoding.bubbletranslation.feature_auth.domain.use_case

import com.bteamcoding.bubbletranslation.app.data.remote.dto.ApiResponse
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto.AuthResponse
import com.bteamcoding.bubbletranslation.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): ApiResponse<AuthResponse> =
        repo.signUp(username, email, password)
}