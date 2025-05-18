package com.bteamcoding.bubbletranslation.feature_camera.domain

import com.google.mlkit.vision.text.Text

data class TranslatedBlock(
    val originalBlock: Text.TextBlock,
    val translatedText: String
)

data class TranslatedVisionText(
    val textBlocks: List<TranslatedBlock>
)
