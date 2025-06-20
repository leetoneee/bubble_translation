package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bteamcoding.bubbletranslation.MainActivity
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.core.components.SelectLang
import com.bteamcoding.bubbletranslation.core.utils.LanguageManager
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.DisplayMode
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModelHolder
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FullscreenModeViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.TranslateMode
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.WordScreenModeAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.WordScreenModeViewModel
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Utils.Companion.getEmojiFlag
import com.bteamcoding.bubbletranslation.feature_home.component.HexagonButton

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DraggableFloatingWidget(
    state: FloatingWidgetState,
    onClose: () -> Unit,
    onToggleExpand: () -> Unit,
    onModeChange: (TranslateMode) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onClick: () -> Unit,
    onShowLanguageScreenChanged: () -> Unit,
    onShowBeeTranslateChanged: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Theo dõi sự thay đổi của sourceLang và targetLang từ LanguageManager
    val sourceLanguage by LanguageManager.sourceLang.collectAsStateWithLifecycle()
    val targetLanguage by LanguageManager.targetLang.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures (
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
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
            onShowLanguageScreenChanged = onShowLanguageScreenChanged,
            onShowBeeTranslateChanged = onShowBeeTranslateChanged,
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
    onShowLanguageScreenChanged: () -> Unit,
    onShowBeeTranslateChanged: () -> Unit
) {
    if (isExpanded) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .background(Color.White, shape = RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 10.dp)
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

                ModeButton(
                    onClick = {
                        onModeChange(TranslateMode.WORD)
                        onToggleExpand()
                    },
                    icon = R.drawable.look_word,
                    content = "Tra từ trên\nmàn hình",
                    enabled = translateMode == TranslateMode.WORD,
                    buttonColor = colorResource(R.color.pink_medium),
                    contentColor = colorResource(R.color.pink_medium)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp),
                    color = colorResource(R.color.white_dark),
                    thickness = 2.dp
                )

                Button(
                    onClick = onShowLanguageScreenChanged,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.blue_lightest)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // Your interactive content like IconButton, Text, etc.
                        SelectLang(
                            shapeSize = 32.dp,
                            textSize = 12.sp
                        )
                        // Overlay a transparent Box to absorb all pointer events
                        val interactionSource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = rememberRipple(bounded = true), // Use default ripple
                                    onClick = onShowLanguageScreenChanged
                                )
                        )
                    }
                    //source Language
//                    Column(
//
//                    ) {
//                        Text(
//                            text = getEmojiFlag(sourceLanguage.countryIso),
//                            fontWeight = FontWeight.Normal,
//                            fontSize = 12.sp,
//                            color = Color.Black
//                        )
//                        Text(
//                            text = sourceLanguage.countryIso,
//                            fontWeight = FontWeight.Normal,
//                            fontSize = 12.sp,
//                            color = Color.Black
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(16.dp))
//                    HexagonButton(
//                        width = 24.dp,
//                        height = 24.dp,
//                        icon = ImageVector.vectorResource(R.drawable.two_arrow),
//                        backgroundColor = colorResource(R.color.blue_medium),
//                        onClick = {}
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    //target Language
//                    Column(
//
//                    ) {
//                        Text(
//                            text = getEmojiFlag(targetLanguage.countryIso),
//                            fontWeight = FontWeight.Normal,
//                            fontSize = 12.sp,
//                            color = Color.Black
//                        )
//                        Text(
//                            text = targetLanguage.countryIso,
//                            fontWeight = FontWeight.Normal,
//                            fontSize = 12.sp,
//                            color = Color.Black
//                        )
//                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = colorResource(R.color.blue_medium), shape = CircleShape)
                            .clip(CircleShape)
                            .clickable { onToggleExpand() }
                            .size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.curv_arrow),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.Center)
                        )
                    }

                    val context = LocalContext.current
                    Image(
                        painter = painterResource(R.drawable.bee_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable (
                                indication = rememberRipple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                }
                                context.startActivity(intent)
                                onToggleExpand()
                            },
                    )

                    Box(
                        modifier = Modifier
                            .background(color = colorResource(R.color.red_light), shape = CircleShape)
                            .clip(CircleShape)
                            .clickable { onClose() }
                            .size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
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
                    onClick = {
                        if (translateMode == TranslateMode.WORD) {
                            onShowBeeTranslateChanged()
                        } else {
                            onClick()
                        }
                    },
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

                    TranslateMode.WORD -> Image(
                        painter = painterResource(R.drawable.bee_pink),
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
        translateMode = TranslateMode.WORD,
        onToggleExpand = {},
        onModeChange = {},
        onClose = {},
        onClick = {},
        onShowLanguageScreenChanged ={},
        onShowBeeTranslateChanged = {}
    )
}

@Preview
@Composable
fun FloatingWidgetPreview2() {
    FloatingWidget(
        sourceLanguage = Country.Thai,
        targetLanguage = Country.Vietnamese,
        isExpanded = false,
        translateMode = TranslateMode.WORD,
        onToggleExpand = {},
        onModeChange = {},
        onClose = {},
        onClick = {},
        onShowLanguageScreenChanged = {},
        onShowBeeTranslateChanged = {}
    )
}


@Preview(showBackground = true)
@Composable
fun DraggableFloatingWidgetPreview() {
    // Sample state
    val state = FloatingWidgetState(
        isExpanded = true,
        translateMode = TranslateMode.FULLSCREEN,
        isOn = true,
        sourceLanguage = Country.English,
        targetLanguage = Country.Vietnamese,
        displayMode = DisplayMode.GLOBAL
    )

    // Preview of DraggableFloatingWidget
    DraggableFloatingWidget(
        state = state,
        onClose = { /* Close action */ },
        onToggleExpand = { /* Toggle expand action */ },
        onModeChange = { },
        onDrag = { _, _ -> },
        onClick = {  },
        onShowLanguageScreenChanged = {},
        onDragEnd = {},
        onShowBeeTranslateChanged = {}
    )
}