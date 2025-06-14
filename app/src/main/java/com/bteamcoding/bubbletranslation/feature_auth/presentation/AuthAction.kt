package com.bteamcoding.bubbletranslation.feature_auth.presentation

sealed interface AuthAction {
    data class OnUsernameChanged(val value: String) : AuthAction
    data class OnEmailChanged(val value: String) : AuthAction
    data class OnPasswordChanged(val value: String) : AuthAction
    data class OnConfirmPasswordChanged(val value: String) : AuthAction
    data class OnShowConfirmDialog(val value: Boolean) : AuthAction

    data object OnLoginClicked : AuthAction
    data object OnRegisterClicked : AuthAction
    data object OnDeleteAccount : AuthAction
    data object ClearError : AuthAction

    data object OnLoadCurrentUser : AuthAction
    data object OnLogOut : AuthAction

    data object OnLoginSuccess : AuthAction
    data object OnRegisterSuccess : AuthAction
    data object OnDeleteSuccess : AuthAction
}