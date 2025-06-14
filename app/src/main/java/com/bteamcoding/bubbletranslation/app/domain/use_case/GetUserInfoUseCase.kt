package com.bteamcoding.bubbletranslation.app.domain.use_case

import com.bteamcoding.bubbletranslation.app.data.repository.UserRepository
import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User> {
        return userRepository.getUserInfo()
    }
}