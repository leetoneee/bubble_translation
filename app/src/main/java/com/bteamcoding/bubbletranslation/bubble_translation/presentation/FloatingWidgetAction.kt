package com.bteamcoding.bubbletranslation.bubble_translation.presentation

sealed interface FloatingWidgetAction {
    object OnExpand : FloatingWidgetAction
    object onClose : FloatingWidgetAction
}