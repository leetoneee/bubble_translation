package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

enum class TranslateMode {
    FULLSCREEN,
    CROP,
    AUTO
}

data class FloatingWidgetState(
    val isExpanded: Boolean = false,
    val translateMode: TranslateMode = TranslateMode.FULLSCREEN
)
