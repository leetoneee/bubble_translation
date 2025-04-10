package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

sealed interface FloatingWidgetAction {
    data object OnToggleExpand : FloatingWidgetAction
    data object OnClose : FloatingWidgetAction
}