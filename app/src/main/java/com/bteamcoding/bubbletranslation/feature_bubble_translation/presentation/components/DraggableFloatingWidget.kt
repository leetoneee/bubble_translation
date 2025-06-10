package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.SubdirectoryArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.TranslateMode

@Composable
fun DraggableFloatingWidget(
    state: FloatingWidgetState,
    onClose: () -> Unit,
    onToggleExpand: () -> Unit,
    onModeChange: (TranslateMode) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onClick: () -> Unit
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
            isExpanded = state.isExpanded,
            translateMode = state.translateMode,
            onClose = onClose,
            onModeChange = onModeChange,
            onToggleExpand = onToggleExpand,
            onClick = onClick
        )
    }
}

@Composable
fun FloatingWidget(
    isExpanded: Boolean,
    translateMode: TranslateMode,
    onClose: () -> Unit,
    onModeChange: (TranslateMode) -> Unit,
    onToggleExpand: () -> Unit,
    onClick: () -> Unit
) {
    if (isExpanded) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .background(Color.White, shape = RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.FULLSCREEN)
                        onToggleExpand()
                    },
                    icon = R.drawable.baseline_translate_24,
                    content = "Dịch toàn\nmàn hình",
                    enabled = translateMode == TranslateMode.FULLSCREEN,
                    buttonColor = colorResource(R.color.b_green),
                    contentColor = Color.White
                )

                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.CROP)
                        onToggleExpand()
                    },
                    icon = R.drawable.outline_crop_free_24,
                    content = "Dịch một\nphần",
                    enabled = translateMode == TranslateMode.CROP,
                    buttonColor = colorResource(R.color.b_blue),
                    contentColor = Color.White
                )

                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.AUTO)
                        onToggleExpand()
                    },
                    icon = R.drawable.baseline_android_24,
                    content = "Dịch tự\nđộng",
                    enabled = translateMode == TranslateMode.AUTO,
                    buttonColor = colorResource(R.color.b_yellow),
                    contentColor = Color.White
                )

                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.AUDIO)
                        onToggleExpand()
                    },
                    icon = R.drawable.baseline_record_voice_over_24,
                    content = "Dịch âm\nthanh",
                    enabled = translateMode == TranslateMode.AUDIO,
                    buttonColor = colorResource(R.color.b_red),
                    contentColor = Color.White
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(1.dp)
                        .background(color = Color.LightGray)
                )

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    elevation = ButtonDefaults.buttonElevation(8.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.b_gray)),
                ) {
                    Text(
                        "EN",
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "fullscreen",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "VN",
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = colorResource(R.color.b_gray), shape = CircleShape)
                            .clip(CircleShape)
                            .clickable { onToggleExpand() }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SubdirectoryArrowLeft,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(color = colorResource(R.color.b_gray), shape = CircleShape)
                            .clip(CircleShape)
                            .clickable { onClose() }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(50.dp) // Kích thước hình tròn cho vùng màu trắng
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color.LightGray, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                // Xử lý khi người dùng long press
                                Log.i("OnToggleExpand", "đã nhấn")
                                onToggleExpand()
                            },
                            onTap = {
                                onClick()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                when (translateMode) {
                    TranslateMode.FULLSCREEN -> Image(
                        painter = painterResource(R.drawable.baseline_translate_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorResource(R.color.b_green))
                    )

                    TranslateMode.CROP -> Image(
                        painter = painterResource(R.drawable.outline_crop_free_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorResource(R.color.b_blue))
                    )

                    TranslateMode.AUTO -> Image(
                        painter = painterResource(R.drawable.baseline_android_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorResource(R.color.b_yellow))
                    )

                    TranslateMode.AUDIO -> Image(
                        painter = painterResource(R.drawable.baseline_record_voice_over_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorResource(R.color.b_red))
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun FloatingWidgetPreview() {
    FloatingWidget(
        isExpanded = true,
        translateMode = TranslateMode.FULLSCREEN,
        onToggleExpand = {},
        onModeChange = {},
        onClose = {},
        onClick = {}
    )

}

@Preview
@Composable
fun FloatingWidgetPreview2() {
    FloatingWidget(
        isExpanded = false,
        translateMode = TranslateMode.AUDIO,
        onToggleExpand = {},
        onModeChange = {},
        onClose = {},
        onClick = {}
    )
}
