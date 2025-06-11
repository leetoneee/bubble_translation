package com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto

import com.bteamcoding.bubbletranslation.feature_auth.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class SignInRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val authenticated: Boolean,
    val user: User
)