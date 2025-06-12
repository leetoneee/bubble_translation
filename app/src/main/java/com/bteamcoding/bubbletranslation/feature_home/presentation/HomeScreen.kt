package com.bteamcoding.bubbletranslation.feature_home.presentation

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.core.components.SelectLang
import com.bteamcoding.bubbletranslation.core.components.TopBar
import com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case.StartFloatingWidgetUseCase
import com.bteamcoding.bubbletranslation.feature_home.component.HexagonButton
import com.bteamcoding.bubbletranslation.feature_home.component.TransModeButton
import com.bteamcoding.bubbletranslation.ui.theme.Inter


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
        }
    )
}

@Composable
fun HomeScreen(
    onShowWidget: () -> Unit,
) {
    var enabled by remember { mutableStateOf(true) }
    Column (
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
            TopBar("Screen Translate")
        }
        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        )  {
            item{
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 54.dp),
                    ){
                        SelectLang()
                    }
                }
            }
            item{
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
                        onClick = {enabled = !enabled},
                        icon = R.drawable.baseline_android_24,
                        content = "Toàn màn hình",
                        description = "Dịch tất cả nội dung xuất hiện trên màn hình",
                        enabled = enabled,
                        contentColor = colorResource(R.color.blue_medium)
                    )
                    TransModeButton(
                        onClick = {enabled = !enabled},
                        icon = R.drawable.baseline_android_24,
                        content = "Một phần màn hình",
                        description = "Chỉ dịch nội dung trong vùng được chọn",
                        enabled = enabled,
                        contentColor = colorResource(R.color.green_medium)
                    )
                    TransModeButton(
                        onClick = {enabled = !enabled},
                        icon = R.drawable.baseline_android_24,
                        content = "Tự động dịch Phụ đề",
                        description = "Tự động dịch Phụ đề xuất hiện trong video, cutscene",
                        enabled = enabled,
                        contentColor = colorResource(R.color.yellow_medium)
                    )
                    TransModeButton(
                        onClick = {enabled = !enabled},
                        icon = R.drawable.baseline_android_24,
                        content = "Tự động dịch Audio",
                        description = "Tự động dịch Audio phát ra từ loa thiết bị",
                        enabled = enabled,
                        contentColor = colorResource(R.color.purple_dark)
                    )
                }
            }
            item{
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
                        onClick = {enabled = !enabled},
                        icon = R.drawable.baseline_android_24,
                        content = "Tiêu chuẩn",
                        description = "Phù hợp cho hầu hết các trường hợp",
                        enabled = enabled,
                        contentColor = colorResource(R.color.blue_dark)
                    )
                    TransModeButton(
                        onClick = {enabled = !enabled},
                        icon = R.drawable.baseline_android_24,
                        content = "Truyện tranh",
                        description = "Tối ưu cho văn bản trong truyện tranh",
                        enabled = enabled,
                        contentColor = colorResource(R.color.blue_dark)
                    )
                    TransModeButton(
                        onClick = {enabled = !enabled},
                        icon = R.drawable.baseline_android_24,
                        content = "Phụ đề",
                        description = "Tối ưu cho phụ đề trong video, cutscene",
                        enabled = enabled,
                        contentColor = colorResource(R.color.blue_dark)
                    )
                }
            }
            item{
                HexagonButton(
                    width = 60.dp,
                    height = 60.dp,
                    icon = Icons.Filled.PowerSettingsNew,
                    backgroundColor = colorResource(R.color.blue_dark),
                    //textStyle = TextStyle(fontSize = 12.sp, color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold),
                    onClick = {onShowWidget()}
                )
            }
        }
    }
}
