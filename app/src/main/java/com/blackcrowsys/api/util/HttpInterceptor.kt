package com.blackcrowsys.api.util

import com.blackcrowsys.util.SharedPreferencesHandler
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class HttpInterceptor(private val sharedPreferencesHandler: SharedPreferencesHandler) : Interceptor {

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return if (sharedPreferencesHandler.getEndpointUrl().isNotBlank()) {
            val host = HttpUrl.parse(sharedPreferencesHandler.getEndpointUrl())
            val newUrl = request.url().newBuilder()
                .host(host?.url()?.toURI()?.host)
                .port(host?.port()!!)
                .build()
            val modifiedRequest = request.newBuilder()
                    .url(newUrl)
                    .build()
            chain.proceed(modifiedRequest)
        } else {
            chain.proceed(request)
        }
    }
}