package com.bteamcoding.bubbletranslation.core.utils

class PartialTextProcessor(private val batchSize: Int) {

    private var lastWordCount = 0
    private var preservedWords: List<String> = emptyList()
    private var isWaiting = false

     fun inputText(text: String): String {
        val words = text.trim().split(Regex("\\s+"))

        val currentWordCount = words.size

        // Nếu số từ chưa vượt quá 12, giữ nguyên toàn bộ
        if (currentWordCount <= batchSize * 2) {
            lastWordCount = currentWordCount
            preservedWords = if (currentWordCount >= batchSize * 2) {
                words.subList(batchSize, batchSize * 2)
            } else {
                emptyList()
            }
            return text
        }

        // Tính ngưỡng mới đã đạt (ví dụ: 12, 18, 24, ...)
        val nextThreshold = ((currentWordCount - 1) / batchSize + 1) * batchSize

//        // Nếu số từ chưa vượt ngưỡng trước đó, chỉ update từ mới nếu vượt lastWordCount
//        if (currentWordCount <= lastWordCount) {
//            return (preservedWords + words.subList(
//                lastWordCount, currentWordCount
//            )).joinToString(" ")
//        }

        // Đạt đến ngưỡng mới (như 18, 24...)
        if (currentWordCount >= nextThreshold) {
            val endIndex = minOf(words.size, nextThreshold)
            val startIndex = maxOf(endIndex - batchSize, 0)
            preservedWords = words.subList(startIndex, endIndex)
            lastWordCount = endIndex
            return preservedWords.joinToString(" ")
        }

        // Khi đang nằm trong khoảng giữa hai ngưỡng
        val newWords = if (lastWordCount < currentWordCount) {
            words.subList(lastWordCount, currentWordCount)
        } else {
            emptyList()
        }

        return (preservedWords + newWords).joinToString(" ")
    }

    fun reset() {
        lastWordCount = 0
        preservedWords = emptyList()
    }
}
