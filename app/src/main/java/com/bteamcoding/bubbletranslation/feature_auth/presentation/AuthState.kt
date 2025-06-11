package com.bteamcoding.bubbletranslation.feature_auth.presentation

import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val user: User? = null,
    val isSignUpSuccessful: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val errorMessage: String? = null,
)
