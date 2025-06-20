package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.bteamcoding.bubbletranslation.core.utils.translateText
import com.google.mlkit.vision.text.Text
import kotlin.math.pow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.DisplayMode
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModelHolder
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText

@Composable
fun TextOverlay(
    visionText: TranslatedVisionText,
    modifier: Modifier = Modifier
) {
    val viewModel = FloatingWidgetViewModelHolder.instance
    val state by viewModel.state.collectAsState()
    val currentDisplay = state.displayMode

    Box(modifier = modifier) {
        visionText.textBlocks.forEach { block ->
            BlockOverlay(
                block = block.originalBlock,
                translatedText = block.translatedText,
                displayMode = currentDisplay
            )
        }
    }
}

@Composable
fun BlockOverlay(
    block: Text.TextBlock,
    translatedText: String,
    displayMode: DisplayMode = DisplayMode.GLOBAL,
) {
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
        ((maxWidthPx * totalHeightPx / totalChars.toFloat()).pow(1f / 2.0f))
    }
    val idealTextSizeSp = with(density) { idealTextSizePx.toSp() }

    // 3. Tính vị trí offset (Dp) và kích thước box (Dp)
    val statusBarHeightPx = remember {
        val resId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) Resources.getSystem().getDimensionPixelSize(resId) else 0
    }

    val offsetDp = with(density) {
        Offset(
            x = (block.boundingBox?.left ?: 0).toDp().value,
            y = ((block.boundingBox?.top ?: 0) - statusBarHeightPx).toDp().value
        )
    }

    val widthDp = with(density) { (maxWidthPx + idealTextSizePx).toDp() }
    val heightDp = with(density) { (totalHeightPx + idealTextSizePx).toDp() }

    // 4. Dịch văn bản bất đồng bộ
//    var translated by remember(block.text) { mutableStateOf("") }
//    LaunchedEffect(block.text) {
//        translated = translateText(block.text)
//    }

    // 5. Hiển thị Text composable
    when {
        displayMode == DisplayMode.GLOBAL -> {
            Text(
                text = translatedText,
                style = TextStyle(
                    color = Color.White,
                    fontSize = idealTextSizeSp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .offset(x = offsetDp.x.dp, y = offsetDp.y.dp)
                    .width(widthDp)
                    .height(heightDp)
                    .background(Color(0xFF000000).copy(alpha = 0.5f))
            )
        }

        displayMode == DisplayMode.COMIC -> {
            Text(
                text = translatedText,
                style = TextStyle(
                    color = Color.Yellow,
                    fontSize = idealTextSizeSp * 1.6, // Slightly larger for visibility
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .offset(x = offsetDp.x.dp, y = offsetDp.y.dp)
                    .width(widthDp * 1.5f) // Slightly smaller for padding
                    .height(heightDp * 1.5f)
            )
        }

        displayMode == DisplayMode.SUBTITLE -> {
            Box(
                modifier = Modifier
                    .offset(x = offsetDp.x.dp, y = offsetDp.y.dp)
                    .width(widthDp * 1.2f)
                    .height(heightDp * 1.2f)
                    .background(Color(0xFF000000).copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = translatedText,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = idealTextSizeSp * 1.5,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}
