package com.blackcrowsys.api

import io.reactivex.Single
import retrofit2.http.GET
import com.blackcrowsys.api.model.IpAddress

interface ApiService {

    @GET(".")
    fun getJsonResponse(): Single<IpAddress>
}