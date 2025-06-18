package com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto

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
    val lasSyncTime: Long,
    val folders: List<WordDto>
)

@Serializable
data class WordSyncResponse(
    val synced: List<WordDto>,
    val conflicts: List<WordDto>
)


