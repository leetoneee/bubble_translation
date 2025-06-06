package com.bteamcoding.bubbletranslation.core.utils

fun getLastWords(text: String, size: Int): String {
    val words = text.split(" ").filter { it.isNotEmpty() }
    val last30Words = if (words.size > size) words.takeLast(size) else words
    return last30Words.joinToString(" ")
}