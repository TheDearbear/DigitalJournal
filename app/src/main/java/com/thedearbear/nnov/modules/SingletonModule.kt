package com.thedearbear.nnov.modules

import android.content.Context
import androidx.room.Room
import com.thedearbear.nnov.AppDatabase
import com.thedearbear.nnov.repositories.LocalAccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) : AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "dj-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideApiAccountRepository(database: AppDatabase) : LocalAccountRepository {
        return LocalAccountRepository(database)
    }
}