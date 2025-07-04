package com.bteamcoding.bubbletranslation.core.utils

import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LanguageManager {
    private var _sourceLang = MutableStateFlow<Country>(Country.English)
    private var _targetLang = MutableStateFlow<Country>(Country.Vietnamese)

    // Publicly exposed StateFlow để các file khác có thể quan sát
    var sourceLang: StateFlow<Country> = _sourceLang
    var targetLang: StateFlow<Country> = _targetLang

    // Hàm để cập nhật sourceLang
    fun updateSourceLanguage(newSourceLanguage: Country) {
        _sourceLang.value = newSourceLanguage
    }

    // Hàm để cập nhật targetLang
    fun updateTargetLanguage(newTargetLanguage: Country) {
        _targetLang.value = newTargetLanguage
    }
}