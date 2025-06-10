package com.bteamcoding.bubbletranslation.feature_auth.presentation

data class AuthState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoginSuccessful: Boolean = false,
    val errorMessage: String? = null,
)
