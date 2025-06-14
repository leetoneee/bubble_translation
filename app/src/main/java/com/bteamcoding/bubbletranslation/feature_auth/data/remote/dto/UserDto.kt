package com.bteamcoding.bubbletranslation.feature_auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserCreationRequest(
    val username: String,
    val email: String,
    val password: String
)

data class UserUpdateRequest(
    val username: String,
)

