package com.bteamcoding.bubbletranslation.core.utils


import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ContextProvider {
    lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }
}
