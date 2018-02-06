package com.blackcrowsys.api

import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/authenticate")
    fun authenticate(@Body authenticationRequest: AuthenticationRequest): Single<AuthenticationResponse>
}