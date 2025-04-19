package com.bteamcoding.bubbletranslation.core.utils

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun translateText(text: String): String {
    return if (text.isNotEmpty()) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH) // Ngôn ngữ nguồn
            .setTargetLanguage(TranslateLanguage.VIETNAMESE) // Dịch sang tiếng Việt
            .build()

        val translator: Translator = Translation.getClient(options)

        // Kiểm tra xem mô hình dịch có cần tải không
        val conditions = DownloadConditions.Builder()
            .requireWifi() // Tải mô hình qua Wi-Fi
            .build()

        // Chờ tải mô hình nếu cần
        suspendCancellableCoroutine<String> { continuation ->
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    Log.d("TranslationSuccess", "Model downloaded successfully.")
                    // Khi mô hình tải thành công, tiếp tục dịch văn bản
                    translator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            Log.d("TranslationSuccess", "Text translated successfully.")
                            continuation.resume(translatedText) // Tiếp tục với kết quả dịch
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "TranslationError",
                                "Text translation failed: ${exception.message}"
                            )
//                            continuation.resumeWithException(exception) // Nếu có lỗi dịch, ném ngoại lệ
                            continuation.resume(text)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("TranslationError", "Model download failed: ${exception.message}")
//                    continuation.resumeWithException(exception) // Nếu có lỗi tải mô hình, ném ngoại lệ
                    continuation.resume(text)

                }

            // Đảm bảo nếu coroutine bị huỷ, ta hủy luôn các tác vụ đang chờ
            continuation.invokeOnCancellation {
                // Hủy bỏ tác vụ nếu coroutine bị huỷ
                translator.close()
            }
        }
    } else {
        // Log nếu văn bản đầu vào là rỗng
        Log.e("TranslationError", "Text is empty, translation not attempted.")
        return text // Nếu văn bản rỗng, trả lại văn bản gốc
    }
}