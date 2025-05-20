package com.bteamcoding.bubbletranslation.feature_camera.presentation

import android.graphics.Bitmap
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.google.mlkit.vision.text.Text

data class PreviewImageState(
    val visionText: Text? = null,
    val translatedVisionText: TranslatedVisionText? = null,
    val isTextVisibility: Boolean = false,
    val imageBitmap: Bitmap? = null,
    val isSpeaking: Boolean = false
)
