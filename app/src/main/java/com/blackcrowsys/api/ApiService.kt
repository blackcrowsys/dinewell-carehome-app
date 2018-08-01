package com.blackcrowsys.api

import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import com.blackcrowsys.api.models.ResidentBioResponse
import com.blackcrowsys.api.models.ResidentResponse
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {

    @POST("/authenticate")
    fun authenticate(@Body authenticationRequest: AuthenticationRequest): Single<AuthenticationResponse>

    @GET("/residents")
    fun getResidents(@Header("Authorization") jwtTokenAuth: String): Single<List<ResidentResponse>>

    @GET("/residents/{residentId}")
    fun getResidentBio(@Header("Authorization") jwtTokenAuth: String, @Path("residentId") residentId: Int): Single<ResidentBioResponse>
}