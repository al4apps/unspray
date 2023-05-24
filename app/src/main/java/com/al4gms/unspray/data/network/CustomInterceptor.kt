package com.al4gms.unspray.data.network

import com.al4gms.unspray.data.AccessToken
import com.al4gms.unspray.data.SavedValues
import okhttp3.Interceptor
import okhttp3.Response

class CustomInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val modifyHeader = originalRequest.headers.newBuilder()
            .add("Authorization", "Bearer ${AccessToken.accessToken}")
            .build()

        SavedValues.incrementRequestCount()
        val modifyRequest = originalRequest.newBuilder()
            .headers(modifyHeader)
            .build()

        return chain.proceed(modifyRequest)
    }
}
