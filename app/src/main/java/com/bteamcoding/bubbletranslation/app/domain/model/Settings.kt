package com.bteamcoding.bubbletranslation.app.domain.model

data class Settings(
    val srcLang: String,
    val tarLang: String,
    val fontSize: Float,
    val backgroundColor: String,
    val textColor: String
)