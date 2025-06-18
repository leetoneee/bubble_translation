package com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FolderDto(
    val id: String,
    val name: String,
    val userId: Long,
    val updatedAt: Long,
    val deleted: Boolean
)

@Serializable
data class FolderSyncRequest(
    val lasSyncTime: Long,
    val folders: List<FolderDto>
)

@Serializable
data class FolderSyncResponse(
    val synced: List<FolderDto>,
    val conflicts: List<FolderDto>
)
