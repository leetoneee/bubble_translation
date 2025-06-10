package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.AutoScreenModeState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import com.bteamcoding.bubbletranslation.R
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout

@SuppressLint("LocalContextConfigurationRead", "ConfigurationScreenWidthHeight")
@Composable
fun AutoArea(
    captureRegion: Rect,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    isPlaying: Boolean,
    onChangePlaying: () -> Unit,
) {
    val optionBarHeight = 40
    var yOffsetRow = if ((captureRegion.top.dp - optionBarHeight.dp) > 0.dp) {
        0.dp
    } else {
        captureRegion.bottom.dp
    }

    var yOffsetBoxCon = if ((captureRegion.top.dp - optionBarHeight.dp) > 0.dp) {
        optionBarHeight.dp
    } else {
        captureRegion.top.dp
    }

    var yOffsetBoxCha = if ((captureRegion.top.dp - optionBarHeight.dp) > 0.dp) {
        0.dp
    } else {
        captureRegion.top.dp
    }

    var heightBoxCha = if ((captureRegion.top.dp - optionBarHeight.dp) > 0.dp) {
        (captureRegion.bottom - captureRegion.top + optionBarHeight).dp
    } else {
        (captureRegion.bottom + optionBarHeight).dp
    }

    val borderColor = colorResource(id = R.color.second_primary).copy(alpha = 0.8f)

    Box(
        modifier = Modifier
            .wrapContentSize()
            .offset(
                x = 0.dp,
                y = yOffsetBoxCha
            )
            .height(heightBoxCha)
            .width((captureRegion.right - captureRegion.left).dp)
    )
    {
        Row(
            modifier = Modifier
                .offset(
                    x = 0.dp,
                    y = yOffsetRow - yOffsetBoxCha
                )
                .background(
                    color = borderColor,
                    shape = RoundedCornerShape(
                        topStart = 6.dp,
                        topEnd = 6.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .padding(8.dp)
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.pause else R.drawable.play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onChangePlaying()
                    }
            )
            Icon(
                painter = painterResource(id = R.drawable.edit), // icon cây but
                contentDescription = "Edit",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onEdit() }
            )
            Icon(
                painter = painterResource(id = R.drawable.round_clear_24), // icon dấu X
                contentDescription = "Close",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClose() }
            )
        }
        Box(
            modifier = Modifier
                .offset(
                    x = 0.dp,
                    y = yOffsetBoxCon - yOffsetBoxCha
                )
                .height((captureRegion.bottom - captureRegion.top).dp)
                .width(captureRegion.right.dp - captureRegion.left.dp)
                .border(width = 2.dp, color = borderColor,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 6.dp,
                        bottomStart = 6.dp,
                        bottomEnd = 6.dp
                    )
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AutoAreaPreview() {
    AutoArea(
        captureRegion = Rect(100f, 50f, 300f, 400f),
        onClose = { /* Thao tác khi đóng */ },
        onEdit = { /* Thao tác khi chỉnh sửa */ },
        isPlaying = true,
        onChangePlaying = {},
    )
}