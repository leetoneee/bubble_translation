package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.google.mlkit.vision.text.Text
import androidx.compose.ui.geometry.Rect

data class WordScreenModeState(
    var sourceText: String? = null,
    var translatedText: String? = null,
    var isShowBottomSheet: Boolean = false
)
