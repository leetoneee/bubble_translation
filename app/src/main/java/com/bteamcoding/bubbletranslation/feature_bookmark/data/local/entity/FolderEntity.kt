package com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Folder
import java.util.UUID

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val userId: Long,
    val updatedAt: Long,
    val deleted: Boolean = false
)

fun FolderEntity.toDomain(): Folder {
    return Folder(
        id = id,
        name = name,
        userId = userId,
        updatedAt = updatedAt,
        deleted = deleted
    )
}