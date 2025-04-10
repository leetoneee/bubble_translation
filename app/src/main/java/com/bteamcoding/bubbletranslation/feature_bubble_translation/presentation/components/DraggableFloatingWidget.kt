package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun DraggableFloatingWidget(
    isExpanded: Boolean,
    onClose: () -> Unit,
    onToggleExpand: () -> Unit,
    onDrag: (Float, Float) -> Unit
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
        // Thêm nội dung FloatingWidget ở đây
        FloatingWidget(
            isExpanded = isExpanded,
            onClose = onClose,
            onToggleExpand = onToggleExpand
        )
    }
}

@Composable
fun FloatingWidget(
    isExpanded: Boolean,
    onClose: () -> Unit,
    onToggleExpand: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (isExpanded) 200.dp else 100.dp)
            .background(Color.LightGray)
            .clickable {
                Log.i("OnToggleExpand", "đã nhấn")
            }
    ) {
        if (isExpanded) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Expanded View", color = Color.Black)
                Button(onClick = onClose) {
                    Text("Close")
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Collapsed", color = Color.Black)
            }
        }
    }
}