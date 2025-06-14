package com.bteamcoding.bubbletranslation.feature_auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val password: String?
)