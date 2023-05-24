package com.al4gms.unspray.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ContextModule {

    @Binds
    abstract fun providesContext(application: Application): Context
}
