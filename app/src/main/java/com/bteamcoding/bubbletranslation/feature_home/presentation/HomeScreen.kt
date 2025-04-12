package com.bteamcoding.bubbletranslation.feature_home.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case.StartFloatingWidgetUseCase
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetAction
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetState
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetViewModel
import org.koin.androidx.compose.koinViewModel

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
                startUseCase()
            }
        }
    )
}

@Composable
fun HomeScreen(
    onShowWidget: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            onShowWidget()
        }) {
            Text("Show Floating Widget")
        }
    }
}