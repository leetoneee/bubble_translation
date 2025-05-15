package com.bteamcoding.bubbletranslation.feature_camera.presentation

import android.net.Uri
import androidx.compose.ui.graphics.painter.Painter

interface PreviewImageAction {
    data class SetUri(val uri: Uri) : PreviewImageAction
    data object OnReset : PreviewImageAction
}