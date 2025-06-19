package com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import kotlinx.serialization.Serializable

@Serializable
data class WordDto(
    val id: String,
    val word: String,
    val folderId: String,
    val updatedAt: Long,
    val deleted: Boolean
)

@Serializable
data class WordSyncRequest(
    val lastSyncTime: Long,
    val words: List<Word>
)

@Serializable
data class WordSyncResponse(
    val synced: List<Word>,
    val conflicts: List<Word>
)


