package com.blackcrowsys.api.util

import com.blackcrowsys.util.SharedPreferencesHandler
import okhttp3.Interceptor
import okhttp3.Response

class HttpInterceptor(private val sharedPreferencesHandler: SharedPreferencesHandler) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return if (sharedPreferencesHandler.getEndpointUrl().isNotBlank()) {
            val modifiedRequest = request.newBuilder()
                    .url(sharedPreferencesHandler.getEndpointUrl())
                    .build()
            chain.proceed(modifiedRequest)
        } else {
            chain.proceed(request)
        }
    }
}