package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.content.Intent
import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.MainActivity
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.core.components.SelectLang
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModelHolder
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.TranslateMode
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service.WordScreenModeService

@Composable
fun DraggableTranslateWord(
    state: FloatingWidgetState,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onShowBeeTranslateChanged: (Boolean) -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val viewModel = FloatingWidgetViewModelHolder.instance
    val stateFloatingWidget by viewModel.state.collectAsState()

    // Lấy kích thước màn hình
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    Log.d("DraggableTranslateWord", "screenHeight: $screenHeight")
    Log.d("DraggableTranslateWord", "screenWidth: $screenWidth")

    @OptIn(ExperimentalFoundationApi::class)
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectDragGestures (
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Giới hạn giá trị offsetX và offsetY trong phạm vi màn hình
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidth.toFloat())
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeight.toFloat())
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
            .onSizeChanged { size ->
                val width = size.width
                val height = size.height

                Log.d("DraggableTranslateWord", "Width: $width, Height: $height")
            }
    ) {
        Box(
            modifier = Modifier
//                .size(90.dp) // Kích thước hình tròn cho vùng màu trắng
                .clip(CircleShape)
                .background(Color.Transparent)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, radius = 40.dp),
                    onClick = {
                        onShowBeeTranslateChanged(false) },
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.bee_black_pink),
                contentDescription = null,
            )
        }
    }
}
