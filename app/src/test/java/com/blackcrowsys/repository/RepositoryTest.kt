package com.blackcrowsys.repository

import com.blackcrowsys.api.ApiService
import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import com.blackcrowsys.persistence.dao.UserPermissionDao
import com.blackcrowsys.persistence.entity.UserPermission
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Test

import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.MockitoAnnotations

/**
 * Unit Test for [Repository].
 */
class RepositoryTest {

    @Mock
    private lateinit var mockApiService: ApiService

    @Mock
    private lateinit var mockUserPermissionDao: UserPermissionDao

    private lateinit var repository: Repository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = Repository(mockApiService, mockUserPermissionDao)
    }

    @Test
    fun login() {
        Mockito.`when`(mockApiService.authenticate(AuthenticationRequest("test", "password"))).thenReturn(
            Single.just(AuthenticationResponse("test", "ekAjdjanKK0KAlsld=", true, mapOf("Residents" to "Read", "Incidents" to "Read"))))

        doNothing().`when`(mockUserPermissionDao).saveUserPermission(UserPermission("Residents", "Read"))
        doNothing().`when`(mockUserPermissionDao).saveUserPermission(UserPermission("Incidents", "Read"))

        val testObserver = TestObserver<AuthenticationResponse>()

        repository.login(AuthenticationRequest("test", "password"))
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it -> it.jwtToken=="ekAjdjanKK0KAlsld=" }
    }

}