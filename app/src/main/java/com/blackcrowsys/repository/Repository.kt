package com.blackcrowsys.repository

import io.reactivex.Single
import com.blackcrowsys.api.ApiService
import com.blackcrowsys.api.model.IpAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val apiService: ApiService) {

    fun getDataFromApi(): Single<IpAddress> = apiService.getJsonResponse()

}