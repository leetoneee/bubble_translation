package com.bteamcoding.bubbletranslation.core.utils

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.bteamcoding.bubbletranslation.core.utils.LanguageManager
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

suspend fun recognizeTextFromImage(image: Bitmap): Text =
    suspendCancellableCoroutine { continuation ->
        val sourceLang = LanguageManager.sourceLang.value
        Log.d("Translation", "Source Language: ${sourceLang.countryName}") // Log ngôn ngữ nguồn

//        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val recognizer = when ( sourceLang.countryIso ) {

            // Nếu ngôn ngữ là chữ Trung Quốc (Chinese script)
            "CN" -> {
                // Chữ Trung Quốc
                TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
            }

            // Nếu ngôn ngữ là chữ Hàn Quốc (Korean script)
            "KR" -> {
                // Chữ Hàn Quốc
                TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            }

            // Nếu ngôn ngữ là chữ Nhật Bản (Japanese script)
            "JP" -> {
                // Chữ Nhật Bản
                TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
            }

            // Nếu ngôn ngữ là chữ Ấn Độ (Devanagari script)
            "IN" -> {
                // Chữ Devanagari
                TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
            }

            else -> {
                // Nếu không thuộc các nhóm trên, sử dụng TextRecognizer mặc định (Latinh script)
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            }
        }

        val inputImage = InputImage.fromBitmap(image, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                Log.d("OCR_FULL_TEXT", visionText.text)
                continuation.resume(visionText)  // Trả kết quả về coroutine
            }
            .addOnFailureListener { exception ->
                Log.e("OCR_ERROR", "Failed to recognize text: ${exception.message}")
                continuation.resumeWithException(exception)  // Ném lỗi nếu có
            }

        continuation.invokeOnCancellation {
            recognizer.close() // Giải phóng nếu coroutine bị huỷ
        }
    }