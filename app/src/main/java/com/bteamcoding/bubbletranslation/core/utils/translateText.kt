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
import com.bteamcoding.bubbletranslation.core.utils.LanguageManager
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country

suspend fun translateText(text: String): String {
    return if (text.isNotEmpty()) {
        val sourceLang = LanguageManager.sourceLang.value
        val targetLang = LanguageManager.targetLang.value

        // Lấy mã ngôn ngữ từ countryName
        val sourceLangCode = getMlKitLanguage(sourceLang)
        val targetLangCode = getMlKitLanguage(targetLang)

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode) // Ngôn ngữ nguồn
            .setTargetLanguage(targetLangCode) // Ngôn ngữ đích
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

fun getMlKitLanguage(country: Country): String {
    return when (country) {
        Country.Afrikaans -> TranslateLanguage.AFRIKAANS
        Country.Arabic -> TranslateLanguage.ARABIC
        Country.Belarusian -> TranslateLanguage.BELARUSIAN
        Country.Bulgarian -> TranslateLanguage.BULGARIAN
        Country.Bengali -> TranslateLanguage.BENGALI
        Country.Catalan -> TranslateLanguage.CATALAN
        Country.Czech -> TranslateLanguage.CZECH
        Country.Danish -> TranslateLanguage.DANISH
        Country.German -> TranslateLanguage.GERMAN
        Country.Greek -> TranslateLanguage.GREEK
        Country.English -> TranslateLanguage.ENGLISH
        Country.Spanish -> TranslateLanguage.SPANISH
        Country.Estonian -> TranslateLanguage.ESTONIAN
        Country.Persian -> TranslateLanguage.PERSIAN
        Country.Finnish -> TranslateLanguage.FINNISH
        Country.French -> TranslateLanguage.FRENCH
        Country.Irish -> TranslateLanguage.IRISH
        Country.Galician -> TranslateLanguage.GALICIAN
        Country.Gujarati -> TranslateLanguage.GUJARATI
        Country.Hebrew -> TranslateLanguage.HEBREW
        Country.Hindi -> TranslateLanguage.HINDI
        Country.Croatian -> TranslateLanguage.CROATIAN
        Country.Haitian -> TranslateLanguage.HAITIAN_CREOLE
        Country.Hungarian -> TranslateLanguage.HUNGARIAN
        Country.Indonesian -> TranslateLanguage.INDONESIAN
        Country.Icelandic -> TranslateLanguage.ICELANDIC
        Country.Italian -> TranslateLanguage.ITALIAN
        Country.Japanese -> TranslateLanguage.JAPANESE
        Country.Georgian -> TranslateLanguage.GEORGIAN
        Country.Kannada -> TranslateLanguage.KANNADA
        Country.Korean -> TranslateLanguage.KOREAN
        Country.Lithuanian -> TranslateLanguage.LITHUANIAN
        Country.Latvian -> TranslateLanguage.LATVIAN
        Country.Macedonian -> TranslateLanguage.MACEDONIAN
        Country.Marathi -> TranslateLanguage.MARATHI
        Country.Malay -> TranslateLanguage.MALAY
        Country.Maltese -> TranslateLanguage.MALTESE
        Country.Dutch -> TranslateLanguage.DUTCH
        Country.Norwegian -> TranslateLanguage.NORWEGIAN
        Country.Polish -> TranslateLanguage.POLISH
        Country.Portuguese -> TranslateLanguage.PORTUGUESE
        Country.Romanian -> TranslateLanguage.ROMANIAN
        Country.Russian -> TranslateLanguage.RUSSIAN
        Country.Slovak -> TranslateLanguage.SLOVAK
        Country.Slovenian -> TranslateLanguage.SLOVENIAN
        Country.Albanian -> TranslateLanguage.ALBANIAN
        Country.Swedish -> TranslateLanguage.SWEDISH
        Country.Swahili -> TranslateLanguage.SWAHILI
        Country.Tamil -> TranslateLanguage.TAMIL
        Country.Telugu -> TranslateLanguage.TELUGU
        Country.Thai -> TranslateLanguage.THAI
        Country.Tagalog -> TranslateLanguage.TAGALOG
        Country.Turkish -> TranslateLanguage.TURKISH
        Country.Ukrainian -> TranslateLanguage.UKRAINIAN
        Country.Urdu -> TranslateLanguage.URDU
        Country.Vietnamese -> TranslateLanguage.VIETNAMESE
        Country.Chinese -> TranslateLanguage.CHINESE
        // Thêm các ngôn ngữ khác nếu cần
        else -> TranslateLanguage.ENGLISH // Mặc định nếu không tìm thấy ngôn ngữ
    }
}