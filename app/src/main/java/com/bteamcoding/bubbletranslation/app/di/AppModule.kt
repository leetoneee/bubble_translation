package com.bteamcoding.bubbletranslation.app.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.AppDatabase
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.dao.FolderDao
import com.bteamcoding.bubbletranslation.feature_bookmark.data.local.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bookmark_db"
        ).build()
    }

    @Provides
    fun provideFolderDao(db: AppDatabase): FolderDao = db.folderDao()

    @Provides
    fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()
}