package com.bteamcoding.bubbletranslation.feature_bookmark.domain.model

import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity.WordEntity
import kotlinx.serialization.Serializable

@Serializable
data class Word (
    val id: String,
    val word: String,
    val folderId: String,
    val updatedAt: Long,
    val deleted: Boolean
)

fun Word.toEntity(): WordEntity {
    return WordEntity(
        id = id,
        word = word,
        folderId = folderId,
        updatedAt = updatedAt,
        deleted = deleted
    )
}