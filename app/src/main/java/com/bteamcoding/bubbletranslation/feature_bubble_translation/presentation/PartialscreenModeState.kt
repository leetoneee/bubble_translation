package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation

import com.google.mlkit.vision.text.Text

data class PartialScreenModeState(
    val visionText: Text? = null,
    val captureRegion: CaptureRegion? = CaptureRegion(50, 50, 100, 100)
)

data class CaptureRegion(
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int
)
