package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.bteamcoding.bubbletranslation.core.utils.translateText
import com.google.mlkit.vision.text.Text
import kotlin.math.pow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.compose.testing.toDp

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun TextOverlayAuto(
    visionText: Text,
    captureRegion: Rect,
    modifier: Modifier = Modifier,
    onDrag: (Float, Float) -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current.density

    Box(modifier = modifier
        .background(Color(0xFF000000).copy(alpha = 0.7f))
        .width((captureRegion.right - captureRegion.left).dp)
        .height(((captureRegion.bottom - captureRegion.top) / 1).dp)
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                offsetX += dragAmount.x
                offsetY += dragAmount.y
                onDrag(dragAmount.x, dragAmount.y)
            }
        }) {
        visionText.textBlocks.forEach { block ->
            val blockOffsetX = (block.boundingBox?.left ?: 0 ).toDp()
            val blockOffsetY = (block.boundingBox?.top ?: 0).toDp()

            Box(
                modifier = Modifier
                    .offset(x = blockOffsetX, y = blockOffsetY / 1 )
            ) {
                BlockOverlayAuto(block = block, captureRegion = captureRegion)
            }
        }
    }
}

@Composable
fun BlockOverlayAuto(block: Text.TextBlock, captureRegion: Rect) {
    val density = LocalDensity.current

    // 1. Tính tổng chiều cao (px) và max chiều rộng (px) của block
    val (totalHeightPx, maxWidthPx) = remember(block) {
        var totalH = 0
        var maxW = 0
        block.lines.forEach { line ->
            val lineH = line.boundingBox?.height() ?: 0
            var lineW = 0
            line.elements.forEachIndexed { idx, elem ->
                val box = elem.boundingBox ?: return@forEachIndexed
                lineW += box.width()
                if (idx > 0) {
                    val prev = line.elements[idx - 1].boundingBox ?: return@forEachIndexed
                    lineW += box.left - prev.right
                }
            }
            totalH += lineH
            if (lineW > maxW) maxW = lineW
        }
        totalH to maxW
    }

    // 2. Tính idealTextSize (px) và convert sang sp
    val totalChars = block.text.length.coerceAtLeast(1)
    val idealTextSizePx = remember(totalHeightPx, maxWidthPx, totalChars) {
        ((maxWidthPx * totalHeightPx / totalChars.toFloat()).pow(1f / 1.9f))
    }
    val idealTextSizeSp = with(density) { idealTextSizePx.toSp() }

    // 3. Tính vị trí offset (Dp) và kích thước box (Dp)
//    val statusBarHeightPx = remember {
//        val resId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
//        if (resId > 0) Resources.getSystem().getDimensionPixelSize(resId) else 0
//    }
//
//    val statusBarHeightDp = with(density) { statusBarHeightPx.toDp() }

    val offsetDp = with(density) {
        Offset(
            x = ((block.boundingBox?.left ?: 0 ).toDp().value ),
            y = ((block.boundingBox?.top ?: 0).toDp().value )
        )
    }

    val widthDp = with(density) { (maxWidthPx + idealTextSizePx).toDp() }
    val heightDp = with(density) { (totalHeightPx + idealTextSizePx).toDp() }

    // 4. Dịch văn bản bất đồng bộ
    var translated by remember(block.text) { mutableStateOf("") }
    LaunchedEffect(block.text) {
        translated = translateText(block.text)
    }

    // 5. Hiển thị Text composable
    Text(
        text = translated,
        style = TextStyle(
            color = Color.White,
            fontSize = idealTextSizeSp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
//            .offset(x = offsetDp.x.dp, y = offsetDp.y.dp + 40.dp)
            .width(widthDp)
            .height(heightDp)
//            .background(Color(0xFF000000).copy(alpha = 0.7f))
    )
}