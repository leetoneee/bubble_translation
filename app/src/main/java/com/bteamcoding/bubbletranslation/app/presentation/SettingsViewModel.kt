package com.bteamcoding.bubbletranslation.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bteamcoding.bubbletranslation.app.domain.model.Settings
import com.bteamcoding.bubbletranslation.app.domain.use_case.GetSettingsUseCase
import com.bteamcoding.bubbletranslation.app.domain.use_case.SetSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getSettings: GetSettingsUseCase,
    private val setSettings: SetSettingsUseCase
) : ViewModel() {
    private val _settings = MutableStateFlow(
        Settings(
            srcLang = "en",
            tarLang = "vi",
            fontSize = 16f,
            backgroundColor = "#FFFFFF",
            textColor = "#000000"
        )
    )
    val settings: StateFlow<Settings> = _settings

    init {
        viewModelScope.launch {
            getSettings().collect { settings ->
                _settings.value = settings
            }
        }
    }

    fun updateSettings(settings: Settings) {
        viewModelScope.launch {
            setSettings(settings)
        }
    }
}