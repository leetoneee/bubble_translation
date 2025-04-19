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

suspend fun recognizeTextFromImage(image: Bitmap): Text =
    suspendCancellableCoroutine { continuation ->
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
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