package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.CaptureRegion

@Composable
fun CropArea(
    modifier: Modifier = Modifier,
    captureRegion: CaptureRegion,
    onCaptureRegionChange: (CaptureRegion) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(2.dp, Color.Blue) // Khung biên màu xanh
    ) {
        // Các góc có thể kéo
        DraggableCorner(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onDrag = { deltaX, deltaY ->
                val newRegion = captureRegion.copy(
                    startX = (captureRegion.startX + deltaX).coerceIn(0f, 500f).toInt(),
                    startY = (captureRegion.startY + deltaY).coerceIn(0f, 500f).toInt()
                )
                onCaptureRegionChange(newRegion)
            }
        )

        DraggableCorner(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onDrag = { deltaX, deltaY ->
                val newRegion = captureRegion.copy(
                    endX = (captureRegion.endX + deltaX).coerceIn(0f, 500f).toInt(),
                    startY = (captureRegion.startY + deltaY).coerceIn(0f, 500f).toInt()
                )
                onCaptureRegionChange(newRegion)
            }
        )

        DraggableCorner(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            onDrag = { deltaX, deltaY ->
                val newRegion = captureRegion.copy(
                    startX = (captureRegion.startX + deltaX).coerceIn(0f, 500f).toInt(),
                    endY = (captureRegion.endY + deltaY).coerceIn(0f, 500f).toInt()
                )
                onCaptureRegionChange(newRegion)
            }
        )

        DraggableCorner(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onDrag = { deltaX, deltaY ->
                val newRegion = captureRegion.copy(
                    endX = (captureRegion.endX + deltaX).coerceIn(0f, 500f).toInt(),
                    endY = (captureRegion.endY + deltaY).coerceIn(0f, 500f).toInt()
                )
                onCaptureRegionChange(newRegion)
            }
        )

        // Các cạnh bên có thể kéo
        DraggableSide(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp),
            onDrag = { deltaX, deltaY ->
                val newRegion = captureRegion.copy(
                    startX = (captureRegion.startX + deltaX).coerceIn(0f, 500f).toInt()
                )
                onCaptureRegionChange(newRegion)
            }
        )

        DraggableSide(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            onDrag = { deltaX, deltaY ->
                val newRegion = captureRegion.copy(
                    endX = (captureRegion.endX + deltaX).coerceIn(0f, 500f).toInt()
                )
                onCaptureRegionChange(newRegion)
            }
        )

        DraggableSide(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            onDrag = { deltaX, deltaY ->
                val newRegion = captureRegion.copy(
                    startY = (captureRegion.startY + deltaY).coerceIn(0f, 500f).toInt()
                )
                onCaptureRegionChange(newRegion)
            }
        )

        DraggableSide(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            onDrag = { deltaX, deltaY ->
                val newRegion = captureRegion.copy(
                    endY = (captureRegion.endY + deltaY).coerceIn(0f, 500f).toInt()
                )
                onCaptureRegionChange(newRegion)
            }
        )
    }
}

@Composable
fun DraggableCorner(
    modifier: Modifier = Modifier,
    onDrag: (Float, Float) -> Unit
) {
    val offset = remember { mutableStateOf(Offset(0f, 0f)) }
    Box(
        modifier = modifier
            .size(32.dp)
            .background(Color.Blue)
            .offset { IntOffset(offset.value.x.toInt(), offset.value.y.toInt()) } // Sử dụng offset.value để truy xuất giá trị
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    offset.value = Offset(offset.value.x + dragAmount.x, offset.value.y + dragAmount.y) // Cập nhật giá trị của offset
                    onDrag(offset.value.x, offset.value.y)
                }
            }
    )
}

@Composable
fun DraggableSide(
    modifier: Modifier = Modifier,
    onDrag: (Float, Float) -> Unit
) {
    val offset = remember { mutableStateOf(Offset(0f, 0f)) }
    Box(
        modifier = modifier
            .size(32.dp)
            .background(Color.Blue)
            .offset { IntOffset(offset.value.x.toInt(), offset.value.y.toInt()) } // Sử dụng offset.value để truy xuất giá trị
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    offset.value = Offset(offset.value.x + dragAmount.x, offset.value.y + dragAmount.y) // Cập nhật giá trị của offset
                    onDrag(offset.value.x, offset.value.y)
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCropArea() {
    // Tạo một CaptureRegion mặc định để kiểm tra preview
    val captureRegion = CaptureRegion(startX = 100, startY = 100, endX = 500, endY = 500)

    // Gọi CropArea để xem UI
    CropArea(
        captureRegion = captureRegion,
        onCaptureRegionChange = { newRegion ->
            // Cập nhật khu vực chụp (mặc dù trong preview, chúng ta không cần thực sự thay đổi nó)
        }
    )
}
