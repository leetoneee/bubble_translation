package com.bteamcoding.bubbletranslation.app.domain.use_case

import com.bteamcoding.bubbletranslation.app.data.repository.UserRepository
import javax.inject.Inject

class SaveLastSyncTimeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(time: Long) {
        userRepository.saveLastSyncTime(time)
    }
}