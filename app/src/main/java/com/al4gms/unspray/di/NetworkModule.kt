package com.al4gms.unspray.di

import com.al4gms.unspray.data.network.CustomInterceptor
import com.al4gms.unspray.data.network.UnsplashApi
import com.al4gms.unspray.service.Downloader
import com.al4gms.unspray.service.DownloaderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun providesOkhttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(CustomInterceptor())
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//        .addNetworkInterceptor(FlipperOkhttpInterceptor(flipperNetworkPlugin))
            .build()
    }

    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun providesApi(retrofit: Retrofit): UnsplashApi {
        return retrofit.create()
    }

    @Provides
    fun providesDownloader(downloader: DownloaderImpl): Downloader = downloader
}
