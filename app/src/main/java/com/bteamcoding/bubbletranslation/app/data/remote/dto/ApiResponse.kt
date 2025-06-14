package com.bteamcoding.bubbletranslation.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T> (
    val code: Int,
    val message: String? = null,
    val result: T? = null
)