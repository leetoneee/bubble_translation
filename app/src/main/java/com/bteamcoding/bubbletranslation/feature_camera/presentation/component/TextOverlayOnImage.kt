package com.bteamcoding.bubbletranslation.feature_camera.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bteamcoding.bubbletranslation.core.utils.translateText
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.google.mlkit.vision.text.Text
import kotlin.math.pow

@Composable
fun TextOverlayOnImage(
    visionText: TranslatedVisionText,
    imageSize: IntSize,
    originalImageSize: Size,
    modifier: Modifier = Modifier
) {
    if (imageSize != IntSize.Zero) {
        val scale = remember(imageSize, originalImageSize) {
            minOf(
                imageSize.width.toFloat() / originalImageSize.width,
                imageSize.height.toFloat() / originalImageSize.height
            )
        }

        val offsetX = remember(imageSize, originalImageSize, scale) {
            (imageSize.width - originalImageSize.width * scale) / 2f
        }
        val offsetY = remember(imageSize, originalImageSize, scale) {
            (imageSize.height - originalImageSize.height * scale) / 2f
        }

        visionText.textBlocks.forEach { block ->
            BlockOverlayOnImage(
                block = block.originalBlock,
                translatedTextBlock =block.translatedText,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY
            )
        }
    }
}

@Composable
fun BlockOverlayOnImage(
    block: Text.TextBlock,
    translatedTextBlock: String,
    scale: Float,
    offsetX: Float,
    offsetY: Float
) {
    val density = LocalDensity.current

    // Tính tổng chiều cao và chiều rộng text block
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

    val totalChars = block.text.length.coerceAtLeast(1)
    val idealTextSizePx = remember(totalHeightPx, maxWidthPx, totalChars) {
        ((maxWidthPx * totalHeightPx / totalChars.toFloat()).pow(0.5f))
    }
    val idealTextSizeSp = with(density) { idealTextSizePx.toSp() }

    val scaledLeftPx = (block.boundingBox?.left ?: 0) * scale + offsetX
    val scaledTopPx = (block.boundingBox?.top ?: 0) * scale + offsetY
    val scaledWidthPx = (maxWidthPx + idealTextSizePx) * scale
    val scaledHeightPx = (totalHeightPx + idealTextSizePx) * scale

//    var translated by remember(block.text) { mutableStateOf("") }
//    LaunchedEffect(block.text) {
//        translated = translateText(block.text)
//    }

    Box(
        modifier = Modifier
            .absoluteOffset(
                x = with(density) { scaledLeftPx.toDp() },
                y = with(density) { scaledTopPx.toDp() }
            )
            .size(
                width = with(density) { scaledWidthPx.toDp() },
                height = with(density) { scaledHeightPx.toDp() }
            )
            .background(Color(0xCCFFFF00), shape = RoundedCornerShape(4.dp))
            .border(1.dp, Color.Red)
            .padding(2.dp)
            .zIndex(10f)
    ) {
        Text(
            text = translatedTextBlock,
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                fontSize = idealTextSizeSp
            )
        )
    }
}
