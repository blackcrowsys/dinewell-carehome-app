package com.blackcrowsys.repository

import io.reactivex.Single
import com.blackcrowsys.api.ApiService
import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val apiService: ApiService) {

    fun login(authenticationRequest: AuthenticationRequest): Single<AuthenticationResponse> {
        return apiService.authenticate(authenticationRequest)
    }
}