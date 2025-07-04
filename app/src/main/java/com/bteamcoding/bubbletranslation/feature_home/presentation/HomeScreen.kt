package com.bteamcoding.bubbletranslation.feature_home.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QuestionMark
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.app.navigation.NavRoutes
import com.bteamcoding.bubbletranslation.core.components.SelectLang
import com.bteamcoding.bubbletranslation.core.components.TopBar
import com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case.StartFloatingWidgetUseCase
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModelHolder
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.TranslateMode
import com.bteamcoding.bubbletranslation.feature_home.component.HexagonButton
import com.bteamcoding.bubbletranslation.feature_home.component.TransModeButton
import com.bteamcoding.bubbletranslation.ui.theme.Inter
import com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case.StopFloatingWidgetUseCase
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.DisplayMode
import androidx.compose.runtime.State
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components.GuideDialog

fun requestOverlayPermission(context: Context) {
    if (!Settings.canDrawOverlays(context)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
    }
}

@Composable
fun HomeScreenRoot(
    startUseCase: StartFloatingWidgetUseCase,
    onRequestScreenCapturePermission: () -> Unit,
    permissionGranted: State<Boolean>,
    navController: NavController
) {
    val context = LocalContext.current

    HomeScreen(
        onShowWidget = {
            if (!Settings.canDrawOverlays(context)) {
                Log.e("FloatingWidget", "❌ Chưa cấp quyền overlay!")
                requestOverlayPermission(context)
            } else {
                Log.d("FloatingWidget", "✅ Quyền overlay đã được cấp")

                // ✅ Gọi use case để bắt đầu widget
                startUseCase()

                // ✅ Trở về màn hình chính
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        },
        onNavToAuthScreen = {
            navController.navigate(NavRoutes.AUTH)
        },
        onRequestScreenCapturePermission = onRequestScreenCapturePermission,
        permissionGranted = permissionGranted
    )
}

@SuppressLint("ServiceCast")
@Composable
fun HomeScreen(
    onShowWidget: () -> Unit,
    onNavToAuthScreen: () -> Unit,
    onRequestScreenCapturePermission: () -> Unit,
    permissionGranted: State<Boolean>
) {
    val viewModel = FloatingWidgetViewModelHolder.instance
    val state by viewModel.state.collectAsState()
    val currentMode = state.translateMode
    val currentDisplay = state.displayMode
    val isOn = state.isOn
    val context = LocalContext.current
    val stopFWUseCase = remember { StopFloatingWidgetUseCase(context) }

    var waitingForPermission by remember { mutableStateOf(false) }

    var isShowGuideFullScreen by remember { mutableStateOf(false) }
    var isShowGuidePartialScreen by remember { mutableStateOf(false) }
    var isShowGuideAutoScreen by remember { mutableStateOf(false) }
    var isShowGuideAudioScreen by remember { mutableStateOf(false) }
    var isShowGuideWordOnScreen by remember { mutableStateOf(false) }

    var isShowGuideGlobal by remember { mutableStateOf(false) }
    var isShowGuideComic by remember { mutableStateOf(false) }
    var isShowGuideSubtitle by remember { mutableStateOf(false) }

    LaunchedEffect(permissionGranted.value) {
        if (waitingForPermission && permissionGranted.value) {
            onShowWidget()
            viewModel.onAction(FloatingWidgetAction.OnStart)
            waitingForPermission = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
        ) {
            TopBar("Screen Translate", onNavToAuthScreen = onNavToAuthScreen)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "Ngôn ngữ",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorResource(R.color.grey_darkest)
                    )
                    Row(
                        modifier = Modifier
                            .width(160.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SelectLang()
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "Chế độ dịch",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorResource(R.color.grey_darkest)
                    )
                    TransModeButton(
                        onClickIconButton = {isShowGuideFullScreen = true},
                        onClick = {
                            viewModel.onAction(FloatingWidgetAction.OnModeChange(TranslateMode.FULLSCREEN))
                        },
                        icon = R.drawable.full_screen,
                        content = "Toàn màn hình",
                        description = "Dịch tất cả nội dung xuất hiện trên màn hình",
                        enabled = currentMode == TranslateMode.FULLSCREEN,
                        contentColor = colorResource(R.color.blue_medium)
                    )

                    TransModeButton(
                        onClickIconButton = {isShowGuidePartialScreen = true},
                        onClick = {
                            viewModel.onAction(FloatingWidgetAction.OnModeChange(TranslateMode.CROP))
                        },
                        icon = R.drawable.partial_screen,
                        content = "Một phần màn hình",
                        description = "Chỉ dịch nội dung trong vùng được chọn",
                        enabled = currentMode == TranslateMode.CROP,
                        contentColor = colorResource(R.color.green_medium)
                    )

                    TransModeButton(
                        onClickIconButton = {isShowGuideAutoScreen = true},
                        onClick = {
                            viewModel.onAction(FloatingWidgetAction.OnModeChange(TranslateMode.AUTO))
                        },
                        icon = R.drawable.autosubtile,
                        content = "Tự động dịch Phụ đề",
                        description = "Tự động dịch Phụ đề xuất hiện trong video, cutscene",
                        enabled = currentMode == TranslateMode.AUTO,
                        contentColor = colorResource(R.color.yellow_medium)
                    )

                    TransModeButton(
                        onClickIconButton = {isShowGuideAudioScreen = true},
                        onClick = {
                            viewModel.onAction(FloatingWidgetAction.OnModeChange(TranslateMode.AUDIO))
                        },
                        icon = R.drawable.auto_audio,
                        content = "Tự động dịch Audio",
                        description = "Tự động dịch Audio phát ra từ loa thiết bị",
                        enabled = currentMode == TranslateMode.AUDIO,
                        contentColor = colorResource(R.color.purple_dark)
                    )

                    TransModeButton(
                        onClickIconButton = {isShowGuideWordOnScreen = true},
                        onClick = {
                            viewModel.onAction(FloatingWidgetAction.OnModeChange(TranslateMode.WORD))
                        },
                        icon = R.drawable.look_word,
                        content = "Tra cứu từ điển",
                        description = "Tra nghĩa trong từ điển của một từ trên màn hình",
                        enabled = currentMode == TranslateMode.WORD,
                        contentColor = colorResource(R.color.pink_medium)
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "Chế độ hiển thị",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorResource(R.color.grey_darkest)
                    )
                    TransModeButton(
                        onClickIconButton = {isShowGuideGlobal = true},
                        onClick = {
                            viewModel.onAction(FloatingWidgetAction.OnDisplayChange(DisplayMode.GLOBAL))
                        },
                        icon = R.drawable.global,
                        content = "Tiêu chuẩn",
                        description = "Phù hợp cho hầu hết các trường hợp",
                        enabled = currentDisplay == DisplayMode.GLOBAL,
                        contentColor = colorResource(R.color.blue_dark)
                    )
                    TransModeButton(
                        onClickIconButton = {isShowGuideComic = true},
                        onClick = {
                            viewModel.onAction(FloatingWidgetAction.OnDisplayChange(DisplayMode.COMIC))
                        },
                        icon = R.drawable.baseline_menu_book_24,
                        content = "Truyện tranh",
                        description = "Tối ưu cho văn bản trong truyện tranh",
                        enabled = currentDisplay == DisplayMode.COMIC,
                        contentColor = colorResource(R.color.blue_dark)
                    )
                    TransModeButton(
                        onClickIconButton = {isShowGuideSubtitle = true},
                        onClick = {
                            viewModel.onAction(FloatingWidgetAction.OnDisplayChange(DisplayMode.SUBTITLE))
                        },
                        icon = R.drawable.subtitles,
                        content = "Phụ đề",
                        description = "Tối ưu cho phụ đề trong video, cutscene",
                        enabled = currentDisplay == DisplayMode.SUBTITLE,
                        contentColor = colorResource(R.color.blue_dark)
                    )
                }
            }
            item {
                HexagonButton(
                    width = 60.dp,
                    height = 60.dp,
                    icon = if (isOn == false) Icons.Filled.PowerSettingsNew else Icons.Filled.PowerOff,
                    backgroundColor = if (isOn == false) colorResource(R.color.blue_dark) else colorResource(
                        R.color.red_medium
                    ),
                    onClick = {
                        if (isOn==false){
                            if (!permissionGranted.value) {
                                waitingForPermission = true
                                onRequestScreenCapturePermission()
                            } else {
                                onShowWidget()
                                viewModel.onAction(FloatingWidgetAction.OnStart)
                            }
                        }
                        else {
                            stopFWUseCase()
                            viewModel.onAction(FloatingWidgetAction.OnClose);
                        }
                    }
                )
            }
        }

        // Checking each flag to show GuideDialog
        if (isShowGuideFullScreen) {
            GuideDialog(
                onDismissRequest = { isShowGuideFullScreen = false },
                onConfirmation = { isShowGuideFullScreen = false },
                dialogTitle = "Full Screen Mode",
                dialogText = "This mode translates all content displayed on the screen.",
                icon = R.drawable.full_screen,
                contentColor = colorResource(R.color.blue_dark)
            )
        }

        if (isShowGuidePartialScreen) {
            GuideDialog(
                onDismissRequest = { isShowGuidePartialScreen = false },
                onConfirmation = { isShowGuidePartialScreen = false },
                dialogTitle = "Partial Screen Mode",
                dialogText = "This mode translates only the selected content within a specified area.",
                icon = R.drawable.partial_screen,
                contentColor = colorResource(R.color.blue_dark)
            )
        }

        if (isShowGuideAutoScreen) {
            GuideDialog(
                onDismissRequest = { isShowGuideAutoScreen = false },
                onConfirmation = { isShowGuideAutoScreen = false },
                dialogTitle = "Auto Subtitle Mode",
                dialogText = "This mode automatically translates subtitles appearing in videos and cutscenes.",
                icon = R.drawable.autosubtile,
                contentColor = colorResource(R.color.blue_dark)
            )
        }

        if (isShowGuideAudioScreen) {
            GuideDialog(
                onDismissRequest = { isShowGuideAudioScreen = false },
                onConfirmation = { isShowGuideAudioScreen = false },
                dialogTitle = "Audio Mode",
                dialogText = "This mode automatically translates audio played from the device speakers.",
                icon = R.drawable.auto_audio,
                contentColor = colorResource(R.color.blue_dark)
            )
        }

        if (isShowGuideWordOnScreen) {
            GuideDialog(
                onDismissRequest = { isShowGuideWordOnScreen = false },
                onConfirmation = { isShowGuideWordOnScreen = false },
                dialogTitle = "Dictionary Lookup",
                dialogText = "This mode looks up the meaning of a word displayed on the screen.",
                icon = R.drawable.look_word,
                contentColor = colorResource(R.color.blue_dark)
            )
        }

        if (isShowGuideGlobal) {
            GuideDialog(
                onDismissRequest = { isShowGuideGlobal = false },
                onConfirmation = { isShowGuideGlobal = false },
                dialogTitle = "Global Mode",
                dialogText = "This mode is suitable for most cases, displaying content in standard format.",
                icon = R.drawable.global,
                contentColor = colorResource(R.color.blue_dark)
            )
        }

        if (isShowGuideComic) {
            GuideDialog(
                onDismissRequest = { isShowGuideComic = false },
                onConfirmation = { isShowGuideComic = false },
                dialogTitle = "Comic Mode",
                dialogText = "This mode optimizes text for comic book-style content.",
                icon = R.drawable.baseline_menu_book_24,
                contentColor = colorResource(R.color.blue_dark)
            )
        }

        if (isShowGuideSubtitle) {
            GuideDialog(
                onDismissRequest = { isShowGuideSubtitle = false },
                onConfirmation = { isShowGuideSubtitle = false },
                dialogTitle = "Subtitle Mode",
                dialogText = "This mode is optimized for subtitles in videos and cutscenes.",
                icon = R.drawable.subtitles,
                contentColor = colorResource(R.color.blue_dark)
            )
        }
    }
}

