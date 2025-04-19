package com.bteamcoding.bubbletranslation.app.domain.repository

import com.bteamcoding.bubbletranslation.app.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setSettings(settings: Settings)
    fun getSettingsFlow(): Flow<Settings>
}