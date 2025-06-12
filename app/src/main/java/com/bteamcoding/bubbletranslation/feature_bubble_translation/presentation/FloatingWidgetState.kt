package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country

enum class TranslateMode {
    FULLSCREEN,
    CROP,
    AUTO,
    AUDIO
}

data class FloatingWidgetState(
    val isExpanded: Boolean = false,
    val translateMode: TranslateMode = TranslateMode.FULLSCREEN,
    val sourceLanguage: Country = Country.English,
    val targetLanguage: Country = Country.Vietnamese
)
