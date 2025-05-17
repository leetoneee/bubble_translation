package com.bteamcoding.bubbletranslation.feature_camera.presentation

import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text

interface PreviewImageAction {
    data class OnChange(val newText: Text) : PreviewImageAction
    data class OnChangeTextVisibility(val newState: Boolean) : PreviewImageAction
    data class SetImageBitmap(val newBitmap: Bitmap) : PreviewImageAction
    data object OnReset : PreviewImageAction
}