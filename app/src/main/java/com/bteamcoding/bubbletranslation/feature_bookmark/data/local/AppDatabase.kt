package com.bteamcoding.bubbletranslation.feature_bookmark.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.dao.FolderDao
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.dao.WordDao
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity.FolderEntity
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.entity.WordEntity

@Database(entities = [FolderEntity::class, WordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun wordDao(): WordDao
}

