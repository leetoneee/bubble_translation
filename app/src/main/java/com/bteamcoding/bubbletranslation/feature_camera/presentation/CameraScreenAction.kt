package com.bteamcoding.bubbletranslation.feature_camera.presentation

import android.net.Uri
import androidx.compose.ui.graphics.painter.Painter

sealed interface CameraScreenAction {
    data class SetUri(val uri: Uri) : CameraScreenAction
    data object OnReset : CameraScreenAction
}