package com.bteamcoding.bubbletranslation.app.domain.use_case

import com.bteamcoding.bubbletranslation.app.domain.model.Settings
import com.bteamcoding.bubbletranslation.app.domain.repository.SettingsRepository

class SetSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(settings: Settings) {
        repository.setSettings(settings)
    }
}