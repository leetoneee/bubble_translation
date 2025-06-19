package com.bteamcoding.bubbletranslation.feature_bookmark.data.remote.dto

import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
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
    val lastSyncTime: Long,
    val folders: List<Folder>
)

@Serializable
data class FolderSyncResponse(
    val synced: List<Folder>,
    val conflicts: List<Folder>
)
