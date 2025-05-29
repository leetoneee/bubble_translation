package com.bteamcoding.bubbletranslation.core.utils

import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

class PartialTextProcessor {

    private val displayedWords = mutableListOf<String>()
    private var lastProcessedWords = listOf<String>()
    private val buffer = mutableListOf<String>()
    private var delayTriggered = AtomicBoolean(false)

    suspend fun inputText(text: String): String {
        val words = text.trim().split("\\s+".toRegex())

        // Tìm phần mới mà chưa được xử lý
        val newWords = words.drop(displayedWords.size)

        if (newWords.isEmpty()) {
            return displayedWords.joinToString(" ")
        }

        // Thêm từ mới vào buffer
        buffer.clear()
        buffer.addAll(newWords)

        // Nếu buffer chưa đủ 12 từ thì vẫn giữ như cũ
        if (buffer.size < 12) {
            return displayedWords.joinToString(" ")
        }

        // Khi đủ 12 từ, thêm vào kết quả hiển thị
        if (buffer.size >= 12) {
            val stableWords = buffer.take(12)
            displayedWords.addAll(stableWords)

            // Xoá 12 từ đầu ra khỏi buffer
            buffer.subList(0, 12).clear()
            delayTriggered.set(false)  // Reset flag sau mỗi batch 12 từ
        }

        // Nếu có thêm từ sau batch 12 và delay chưa chạy
        if (buffer.isNotEmpty() && !delayTriggered.get()) {
            delayTriggered.set(true)
            delay(1000) // chờ 1 giây

            // Sau khi đợi, hiển thị phần còn lại (nếu có)
            displayedWords.addAll(buffer)
            buffer.clear()
        }

        return displayedWords.joinToString(" ")
    }
}
