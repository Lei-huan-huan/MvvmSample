package com.lhh.mvvmsample.di

import android.content.Context
import androidx.room.Room
import com.lhh.mvvmsample.data.local.AppDatabase
import com.lhh.mvvmsample.data.local.ImageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mvvm_image_db"
        ).build()
    }

    @Provides
    fun provideImageDao(
        appDatabase: AppDatabase
    ): ImageDao {
        return appDatabase.imageDao()
    }
}
