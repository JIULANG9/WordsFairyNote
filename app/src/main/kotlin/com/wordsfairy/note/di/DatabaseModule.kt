package com.wordsfairy.note.di

import android.content.Context
import com.wordsfairy.note.data.room.dao.*
import com.wordsfairy.note.data.room.db.AppDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Desvcription:
 * @Author: JIULANG
 * @Data: 2023/4/27 21:46
 */
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(): AppDataBase {
        return AppDataBase.getInstance()
    }

    @Provides
    fun provideNoteFolderDao(appDatabase: AppDataBase): NoteFolderDao {
        return appDatabase.noteFolderDao()
    }

    @Provides
    fun provideNoteEntityDao(appDatabase: AppDataBase): NoteEntityDao {
        return appDatabase.noteEntityDao()
    }

    @Provides
    fun provideNoteContentDao(appDatabase: AppDataBase): NoteContentDao {
        return appDatabase.noteContentDao()
    }

    @Provides
    fun searchRecordDao(appDatabase: AppDataBase): SearchRecordEntityDao {
        return appDatabase.searchRecordDao()
    }

    @Provides
    fun provideNoteDao(appDatabase: AppDataBase): NoteDao {
        return appDatabase.noteDao()
    }
}