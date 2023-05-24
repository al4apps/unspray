package com.al4gms.unspray.data.network

// import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
// import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object Network {

//    val flipperNetworkPlugin = NetworkFlipperPlugin()

    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(CustomInterceptor())
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//        .addNetworkInterceptor(FlipperOkhttpInterceptor(flipperNetworkPlugin))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()

    val unsplashApi: UnsplashApi
        get() = retrofit.create()
}
