package com.bteamcoding.bubbletranslation.core.utils

fun get30LastWords(text: String): String {
    val words = text.split(" ").filter { it.isNotEmpty() }
    val last30Words = if (words.size > 30) words.takeLast(30) else words
    return last30Words.joinToString(" ")
}