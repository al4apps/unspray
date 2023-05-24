package com.al4gms.unspray.di

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import com.al4gms.unspray.data.db.CollectionsDao
import com.al4gms.unspray.data.db.PhotosDao
import com.al4gms.unspray.data.db.UnsprayDatabase
import com.al4gms.unspray.data.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@ExperimentalPagingApi
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(application: Application): UnsprayDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            UnsprayDatabase::class.java,
            UnsprayDatabase.DB_NAME,
        ).build()
    }

    @Provides
    fun providesUserDao(db: UnsprayDatabase): UserDao = db.userDao()

    @Provides
    fun providesPhotosDao(db: UnsprayDatabase): PhotosDao = db.photosDao()

    @Provides
    fun providesCollectionsDao(db: UnsprayDatabase): CollectionsDao = db.collectionsDao()
}
