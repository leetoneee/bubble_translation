package com.bteamcoding.bubbletranslation.core.utils

import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedBlock
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun translateVisionText(visionText: Text): TranslatedVisionText = coroutineScope {
    val translatedBlocks = visionText.textBlocks.map { block ->
        async {
            val translated = translateText(block.text)
            TranslatedBlock(
                originalBlock = block,
                translatedText = translated
            )
        }
    }.awaitAll()

    TranslatedVisionText(text = visionText.text, textBlocks = translatedBlocks)
}
