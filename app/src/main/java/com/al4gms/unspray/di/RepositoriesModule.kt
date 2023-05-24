package com.al4gms.unspray.di

import androidx.paging.ExperimentalPagingApi
import com.al4gms.unspray.data.repositories.AuthRepository
import com.al4gms.unspray.data.repositories.AuthRepositoryImpl
import com.al4gms.unspray.data.repositories.CollectionsRepository
import com.al4gms.unspray.data.repositories.CollectionsRepositoryImpl
import com.al4gms.unspray.data.repositories.PhotosRepository
import com.al4gms.unspray.data.repositories.PhotosRepositoryImpl
import com.al4gms.unspray.data.repositories.ProfileRepository
import com.al4gms.unspray.data.repositories.ProfileRepositoryImpl
import com.al4gms.unspray.data.repositories.StartingRepository
import com.al4gms.unspray.data.repositories.StartingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@ExperimentalPagingApi
@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {

    @Binds
    @ViewModelScoped
    abstract fun providesPhotosRepository(repositoryImpl: PhotosRepositoryImpl): PhotosRepository

    @Binds
    @ViewModelScoped
    abstract fun providesStartingRepository(repositoryImpl: StartingRepositoryImpl): StartingRepository

    @Binds
    @ViewModelScoped
    abstract fun providesAuthRepository(repositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @ViewModelScoped
    abstract fun providesCollectionsRepository(repositoryImpl: CollectionsRepositoryImpl): CollectionsRepository

    @Binds
    @ViewModelScoped
    abstract fun providesProfileRepository(repositoryImpl: ProfileRepositoryImpl): ProfileRepository
}
