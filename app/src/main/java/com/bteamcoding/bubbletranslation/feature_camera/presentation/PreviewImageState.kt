package com.bteamcoding.bubbletranslation.feature_camera.presentation

import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text

data class PreviewImageState(
    val visionText: Text? = null,
    val isTextVisibility: Boolean = false,
    val imageBitmap: Bitmap? = null
)
