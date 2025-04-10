package com.bteamcoding.bubbletranslation.feature_bubble_translation.domain.use_case

import android.content.Context
import android.content.Intent
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.service.FloatingWidgetService

class StopFloatingWidgetUseCase(private val context: Context) {
    operator fun invoke() {
        val intent = Intent(context, FloatingWidgetService::class.java)
        context.stopService(intent)
    }
}