package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.text.Text

@Composable
fun CoatingLayer(
    text: Text,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0D12).copy(alpha = 0.7f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        offsetY += dragAmount.y
                        onDrag(dragAmount.y)
                    },
                    onDragEnd = {
                        onDragEnd()
                    }
                )
            }
    ) {
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