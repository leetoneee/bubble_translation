package com.bteamcoding.bubbletranslation.feature_auth.presentation

sealed interface AuthAction {
    data class OnUsernameChanged(val value: String) : AuthAction
    data class OnEmailChanged(val value: String) : AuthAction
    data class OnPasswordChanged(val value: String) : AuthAction
    data class OnConfirmPasswordChanged(val value: String) : AuthAction

    data object OnLoginClicked : AuthAction
    data object OnRegisterClicked : AuthAction
    data object ClearError : AuthAction
}