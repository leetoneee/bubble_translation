package com.bteamcoding.bubbletranslation.app.domain.use_case

import com.bteamcoding.bubbletranslation.app.domain.model.Settings
import com.bteamcoding.bubbletranslation.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<Settings> = repository.getSettingsFlow()
}