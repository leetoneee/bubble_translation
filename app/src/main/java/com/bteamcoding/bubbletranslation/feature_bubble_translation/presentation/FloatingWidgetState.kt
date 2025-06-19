package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country

enum class TranslateMode {
    FULLSCREEN,
    CROP,
    AUTO,
    AUDIO,
    WORD
}

enum class DisplayMode {
    GLOBAL,
    COMIC,
    SUBTITLE,
}

data class FloatingWidgetState(
    val isExpanded: Boolean = false,
    val translateMode: TranslateMode = TranslateMode.FULLSCREEN,
    val isOn:Boolean = false,
    val sourceLanguage: Country = Country.English,
    val targetLanguage: Country = Country.Vietnamese,
    val displayMode: DisplayMode = DisplayMode.GLOBAL,
)
