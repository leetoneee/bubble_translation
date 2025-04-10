package com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case

import android.content.Context
import android.content.Intent
import android.util.Log
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service.FloatingWidgetService

class StartFloatingWidgetUseCase(private val context: Context) {
    operator fun invoke() {
        Log.d("StartFloatingWidgetUseCase", "invoke() called")  // Log to track function call
        val intent = Intent(context, FloatingWidgetService::class.java)
        context.startService(intent)
    }
}