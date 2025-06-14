package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.core.utils.LanguageManager
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.TranslateMode
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Utils.Companion.getEmojiFlag

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DraggableFloatingWidget(
    state: FloatingWidgetState,
    onClose: () -> Unit,
    onToggleExpand: () -> Unit,
    onModeChange: (TranslateMode) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onClick: () -> Unit,
    onShowLanguageScreenChanged: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Theo dõi sự thay đổi của sourceLang và targetLang từ LanguageManager
    val sourceLanguage by LanguageManager.sourceLang.collectAsStateWithLifecycle()
    val targetLanguage by LanguageManager.targetLang.collectAsStateWithLifecycle()

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
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            isExpanded = state.isExpanded,
            translateMode = state.translateMode,
            onClose = onClose,
            onModeChange = onModeChange,
            onToggleExpand = onToggleExpand,
            onClick = onClick,
            onShowLanguageScreenChanged = onShowLanguageScreenChanged
        )
    }
}

@Composable
fun FloatingWidget(
    sourceLanguage: Country,
    targetLanguage: Country,
    isExpanded: Boolean,
    translateMode: TranslateMode,
    onClose: () -> Unit,
    onModeChange: (TranslateMode) -> Unit,
    onToggleExpand: () -> Unit,
    onClick: () -> Unit,
    onShowLanguageScreenChanged: () -> Unit
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.FULLSCREEN)
                        onToggleExpand()
                    },
                    icon = R.drawable.full_screen,
                    content = "Dịch toàn\nmàn hình",
                    enabled = translateMode == TranslateMode.FULLSCREEN,
                    buttonColor = colorResource(R.color.blue_dark),
                    contentColor = colorResource(R.color.blue_medium)
                )

                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.CROP)
                        onToggleExpand()
                    },
                    icon = R.drawable.partial_screen,
                    content = "Một phần\nmàn hình",
                    enabled = translateMode == TranslateMode.CROP,
                    buttonColor = colorResource(R.color.green_medium),
                    contentColor = colorResource(R.color.green_medium)
                )

                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.AUTO)
                        onToggleExpand()
                    },
                    icon = R.drawable.autosubtile,
                    content = "Dịch sub\ntự động",
                    enabled = translateMode == TranslateMode.AUTO,
                    buttonColor = colorResource(R.color.yellow_medium),
                    contentColor = colorResource(R.color.yellow_medium)
                )

                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.AUDIO)
                        onToggleExpand()
                    },
                    icon = R.drawable.auto_audio,
                    content = "Dịch audio\ntự động",
                    enabled = translateMode == TranslateMode.AUDIO,
                    buttonColor = colorResource(R.color.purple_dark),
                    contentColor = colorResource(R.color.purple_dark)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 4.dp),
                    color = colorResource(R.color.white_dark),
                    thickness = 2.dp
                )

                Button(
                    onClick = onShowLanguageScreenChanged,
                    modifier = Modifier
//                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    elevation = ButtonDefaults.buttonElevation(8.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.b_gray)),
                ) {
                    //source Language
                    Column(

                    ) {
                        Text(
                            text = getEmojiFlag(sourceLanguage.countryIso),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                        Text(
                            text = sourceLanguage.countryIso,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "fullscreen",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    //target Language
                    Column(

                    ) {
                        Text(
                            text = getEmojiFlag(targetLanguage.countryIso),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                        Text(
                            text = targetLanguage.countryIso,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = colorResource(R.color.blue_medium), shape = CircleShape)
                            .clip(CircleShape)
                            .clickable { onToggleExpand() }
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.curv_arrow),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center)
                        )
                    }

                    Image(
                        painter = painterResource(R.drawable.bee_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                    )

                    Box(
                        modifier = Modifier
                            .background(color = colorResource(R.color.blue_medium), shape = CircleShape)
                            .clip(CircleShape)
                            .clickable { onClose() }
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

            }
        }
    } else {
        @OptIn(ExperimentalFoundationApi::class)
        Box(
            modifier = Modifier
                .size(90.dp) // Kích thước hình tròn cho vùng màu trắng
                .clip(CircleShape)
                .background(Color.Transparent)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, radius = 40.dp),
                    onClick = { onClick() },
                    onLongClick = {
                        Log.i("OnToggleExpand", "đã nhấn")
                        onToggleExpand()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onLongPress = {
//                                // Xử lý khi người dùng long press
//                                Log.i("OnToggleExpand", "đã nhấn")
//                                onToggleExpand()
//                            },
//                            onTap = {
//                                onClick()
//                            }
//                        )
//                    },
//                contentAlignment = Alignment.Center,
//            ) {
                when (translateMode) {
                    TranslateMode.FULLSCREEN -> Image(
                        painter = painterResource(R.drawable.bee_blue),
                        contentDescription = null,
                    )

                    TranslateMode.CROP -> Image(
                        painter = painterResource(R.drawable.bee_green),
                        contentDescription = null,
                    )

                    TranslateMode.AUTO -> Image(
                        painter = painterResource(R.drawable.bee_yellow),
                        contentDescription = null,
                    )

                    TranslateMode.AUDIO -> Image(
                        painter = painterResource(R.drawable.bee_purple),
                        contentDescription = null,
                    )
                }
            //}
        }
    }
}

@Preview
@Composable
fun FloatingWidgetPreview() {
    FloatingWidget(
        sourceLanguage = Country.Thai,
        targetLanguage = Country.Vietnamese,
        isExpanded = true,
        translateMode = TranslateMode.FULLSCREEN,
        onToggleExpand = {},
        onModeChange = {},
        onClose = {},
        onClick = {},
        onShowLanguageScreenChanged ={}
    )
}

@Preview
@Composable
fun FloatingWidgetPreview2() {
    FloatingWidget(
        sourceLanguage = Country.Thai,
        targetLanguage = Country.Vietnamese,
        isExpanded = false,
        translateMode = TranslateMode.AUDIO,
        onToggleExpand = {},
        onModeChange = {},
        onClose = {},
        onClick = {},
        onShowLanguageScreenChanged = {}
    )
}
