package com.bteamcoding.bubbletranslation.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bteamcoding.bubbletranslation.app.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SettingsDataStore {
    private val Context.dataStore by preferencesDataStore(name = "settings_prefs")

    private val SRC_LANG = stringPreferencesKey("src_lang")
    private val TAR_LANG = stringPreferencesKey("tar_lang")
    private val FONT_SIZE = floatPreferencesKey("font_size")
    private val BACKGROUND_COLOR = stringPreferencesKey("background_color")
    private val TEXT_COLOR = stringPreferencesKey("text_color")

    suspend fun setSettings(context: Context, settings: Settings) {
        context.dataStore.edit { prefs ->
            prefs[SRC_LANG] = settings.srcLang
            prefs[TAR_LANG] = settings.tarLang
            prefs[FONT_SIZE] = settings.fontSize
            prefs[BACKGROUND_COLOR] = settings.backgroundColor
            prefs[TEXT_COLOR] = settings.textColor
        }
    }

    fun getSettingsFlow(context: Context): Flow<Settings> {
        return context.dataStore.data.map { prefs ->
            Settings(
                srcLang = prefs[SRC_LANG] ?: "en",
                tarLang = prefs[TAR_LANG] ?: "vi",
                fontSize = prefs[FONT_SIZE] ?: 16f,
                backgroundColor = prefs[BACKGROUND_COLOR] ?: "#FFFFFF",
                textColor = prefs[TEXT_COLOR] ?: "#000000"
            )
        }
    }
}