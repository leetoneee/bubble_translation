package com.bteamcoding.bubbletranslation

import android.app.Application
import com.bteamcoding.bubbletranslation.core.utils.ContextProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo ContextProvider với application context
        ContextProvider.init(this)
    }
}