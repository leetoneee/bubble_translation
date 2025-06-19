package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.google.mlkit.vision.text.Text

@Composable
fun CoatingLayer(
    text: TranslatedVisionText,
    isTextVisibility: Boolean,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onChangeVisibility: (Boolean) -> Unit
) {
    var offsetY by remember { mutableFloatStateOf(0f) }
//    var isVisible = remember { mutableStateOf(true) } // ← use mutableStateOf

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0D12).copy(alpha = 0.6f))
            .pointerInput(Unit) {
//                detectTapGestures(
//                    onPress = {
//                        try {
//                            awaitRelease()
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                )
//                detectDragGestures(
//                    onDrag = { _, dragAmount ->
//                        offsetY += dragAmount.y
//                        onDrag(dragAmount.y)
//                    },
//                    onDragEnd = {
//                        onDragEnd()
//                    },
//                )
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        // Handle press
                        val press = event.changes.firstOrNull()
                        if (press != null && press.pressed) {
                            onChangeVisibility(false) // for example
                        }

                        // Handle drag
                        event.changes.forEach { change ->
                            if (change.pressed && change.positionChange() != Offset.Zero) {
                                // isVisible.value = true
                                offsetY += change.positionChange().y
                                onDrag(change.positionChange().y)
                                change.consume()
                            }
                        }

                        // Handle release
                        if (event.changes.all { !it.pressed }) {
                            onDragEnd()
                            onChangeVisibility(true)
                        }
                    }
                }
            }
    ) {
        if (isTextVisibility)
            TextOverlay(visionText = text, modifier = Modifier.fillMaxSize())
    }
}

//@Preview
//@Composable
//fun CoatingLayerPreview() {
//    CoatingLayer(
//        visionText = null,
//        onDrag = {},
//        onDragEnd = {}
//    )
//}