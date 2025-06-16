package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country

sealed interface FloatingWidgetAction {
    data object OnToggleExpand : FloatingWidgetAction
    data object OnClose : FloatingWidgetAction
    data object OnStart : FloatingWidgetAction
    data class OnModeChange(val newMode: TranslateMode) : FloatingWidgetAction
    data class OnDisplayChange(val newMode: DisplayMode) : FloatingWidgetAction
    data class OnSourceLanguageChange(val newSourceLanguage: Country) : FloatingWidgetAction
    data class OnTargetLanguageChange(val newTargetLanguage: Country) : FloatingWidgetAction
}