package com.bteamcoding.bubbletranslation.feature_camera.presentation

import android.graphics.Bitmap
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.google.mlkit.vision.text.Text

sealed interface PreviewImageAction {
    data class OnChange(val newText: Text) : PreviewImageAction
    data class OnChangeTextVisibility(val newState: Boolean) : PreviewImageAction
    data class OnChangeIsSpeaking(val newState: Boolean) : PreviewImageAction
    data class OnChangeTranslatedVisionText(val newText: TranslatedVisionText) : PreviewImageAction
    data class SetImageBitmap(val newBitmap: Bitmap) : PreviewImageAction
    data object OnReset : PreviewImageAction
}