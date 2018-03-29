package com.blackcrowsys.api

import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import com.blackcrowsys.api.models.ResidentResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/authenticate")
    fun authenticate(@Body authenticationRequest: AuthenticationRequest): Single<AuthenticationResponse>

    @GET("/residents")
    fun getResidents(): Single<List<ResidentResponse>>
}