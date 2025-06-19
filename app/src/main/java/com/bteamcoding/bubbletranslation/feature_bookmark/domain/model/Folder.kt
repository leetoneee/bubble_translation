package com.bteamcoding.bubbletranslation.feature_bookmark.domain.model

import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity.FolderEntity
import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val id: String,
    val name: String,
    val userId: Long,
    val updatedAt: Long,
    val deleted: Boolean
)

fun Folder.toEntity(): FolderEntity {
    return FolderEntity(
        id = id,
        name = name,
        userId = userId,
        updatedAt = updatedAt,
        deleted = deleted
    )
}