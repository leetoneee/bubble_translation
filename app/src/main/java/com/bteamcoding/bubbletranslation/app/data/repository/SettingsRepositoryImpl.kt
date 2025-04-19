package com.bteamcoding.bubbletranslation.app.data.repository

import android.content.Context
import com.bteamcoding.bubbletranslation.app.data.local.SettingsDataStore
import com.bteamcoding.bubbletranslation.app.domain.model.Settings
import com.bteamcoding.bubbletranslation.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    override suspend fun setSettings(settings: Settings) {
        SettingsDataStore.setSettings(context, settings)
    }

    override fun getSettingsFlow(): Flow<Settings> {
        return SettingsDataStore.getSettingsFlow(context)
    }
}