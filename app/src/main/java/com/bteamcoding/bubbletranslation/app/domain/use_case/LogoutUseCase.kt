package com.bteamcoding.bubbletranslation.app.domain.use_case

import com.bteamcoding.bubbletranslation.app.data.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.logout()
    }
}