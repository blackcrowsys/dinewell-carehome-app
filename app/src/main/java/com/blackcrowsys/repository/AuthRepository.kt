package com.blackcrowsys.repository

import com.blackcrowsys.api.ApiService
import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import com.blackcrowsys.persistence.dao.UserPermissionDao
import com.blackcrowsys.persistence.entity.UserPermission
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPermissionDao: UserPermissionDao
) {

    fun login(authenticationRequest: AuthenticationRequest): Single<AuthenticationResponse> {
        return apiService.authenticate(authenticationRequest)
            .flatMap { authenticationResponse ->
                authenticationResponse.permissions.forEach { keyValuePair -> userPermissionDao.saveUserPermission(
                    UserPermission(keyValuePair.key, keyValuePair.value))
                }
                Single.just(authenticationResponse)
            }
    }
}