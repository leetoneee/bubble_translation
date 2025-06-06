package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.google.mlkit.vision.text.Text

data class AudioModeState(
    val isRecognizing: Boolean = false,
    val topPosition: Int = 50,
    val recognizedText: String = "",
    val isTranslateMode: Boolean = true
)