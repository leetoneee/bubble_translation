package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun DraggableSubtitleOverlay(
    topPosition: Int,
    isRecognizing: Boolean,
    subtitleText: String,
    isTranslateMode: Boolean,
    onToggleTranslateMode: () -> Unit,
    onStartRecognition: () -> Unit,
    onStopRecognition: () -> Unit,
    onClose: () -> Unit,
    onDrag: (Float, Float) -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
    ) {
        SubtitleOverlay(
            subtitleText = subtitleText,
            topPosition = topPosition,
            isTranslateMode = isTranslateMode,
            isRecognizing = isRecognizing,
            onToggleTranslateMode = onToggleTranslateMode,
            onStartRecognition = onStartRecognition,
            onStopRecognition =onStopRecognition,
            onClose = onClose
        )
    }
}