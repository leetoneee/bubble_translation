package com.bteamcoding.bubbletranslation.app.domain.use_case

import com.bteamcoding.bubbletranslation.app.data.repository.UserRepository
import javax.inject.Inject

class SaveUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Long, username: String, email: String) {
        userRepository.saveUserInfo(id, username, email)
    }
}