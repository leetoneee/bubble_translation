package com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bteamcoding.bubbletranslation.feature_bookmark.domain.model.Word
import java.util.UUID

@Entity(
    tableName = "words",
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("folderId")]
)
data class WordEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val word: String,
    val folderId: String,
    val updatedAt: Long,
    val deleted: Boolean = false
)

fun WordEntity.toDomain(): Word {
    return Word(
        id = id,
        word = word,
        folderId = folderId,
        updatedAt = updatedAt,
        deleted = deleted
    )
}