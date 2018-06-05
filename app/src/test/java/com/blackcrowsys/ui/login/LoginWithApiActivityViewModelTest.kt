package com.blackcrowsys.ui.login

import android.arch.lifecycle.Observer
import com.blackcrowsys.R
import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import com.blackcrowsys.exceptions.EmptyUsernamePasswordException
import com.blackcrowsys.exceptions.ErrorMapper
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.exceptions.InvalidUrlException
import com.blackcrowsys.repository.AuthRepository
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit test for [LoginWithApiActivityViewModel].
 */
@RunWith(RobolectricTestRunner::class)
class LoginWithApiActivityViewModelTest {

    @Mock
    private lateinit var mockAuthRepository: AuthRepository

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    @Mock
    private lateinit var liveDataObserver: Observer<ViewState>

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var loginWithApiActivityViewModel: LoginWithApiActivityViewModel

    @Before
    fun setUp() {
        val exceptionTransformer = ExceptionTransformer(ErrorMapper(RuntimeEnvironment.application))
        MockitoAnnotations.initMocks(this)
        loginWithApiActivityViewModel = LoginWithApiActivityViewModel(
            mockAuthRepository,
            schedulerProvider,
            mockSharedPreferencesHandler,
            exceptionTransformer
        )

        loginWithApiActivityViewModel.viewStateResponse.observeForever(liveDataObserver)
    }

    @Test
    fun `loginWithApi given correct details`() {
        mockApiCall()

        loginWithApiActivityViewModel.loginWithApi("test", "password", "http://testurl.com")

        val successValue =
            loginWithApiActivityViewModel.viewStateResponse.value as ViewState.Success<*>
        assertEquals("etxWWKsjakj9ajsE32X=", successValue.data)

        verify(liveDataObserver).onChanged(ViewState.Success("etxWWKsjakj9ajsE32X="))
    }

    @Test
    fun `loginWithApi given Empty username`() {
        loginWithApiActivityViewModel.loginWithApi("", "password", "http://testurl.com")

        val errorValue = loginWithApiActivityViewModel.viewStateResponse.value as ViewState.Error
        assertEquals(
            RuntimeEnvironment.application.getString(R.string.empty_username_password_error),
            errorValue.throwable.message
        )
    }

    @Test
    fun `loginWithApi given invalid url`() {
        loginWithApiActivityViewModel.loginWithApi("user", "password", "foobar")

        val errorValue = loginWithApiActivityViewModel.viewStateResponse.value as ViewState.Error
        assertEquals(
            RuntimeEnvironment.application.getString(R.string.url_invalid_error),
            errorValue.throwable.message
        )
    }

    @Test
    fun `authenticate With Api`() {
        mockApiCall()

        val testObserver = TestObserver<AuthenticationResponse>()

        loginWithApiActivityViewModel.authenticateWithApi(AuthenticationRequest("test", "password"))
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it ->
            it.jwtToken == "etxWWKsjakj9ajsE32X=" &&
                    it.permissions.containsKey("Residents") &&
                    it.permissions.containsKey("Incidents")
        }
    }

    @Test
    fun `isUrlValid when url is empty`() {
        val testObserver = TestObserver<Any>()

        loginWithApiActivityViewModel.isUrlValid("")
            .subscribe(testObserver)

        testObserver.assertError(InvalidUrlException::class.java)
    }

    @Test
    fun `isUrlValidWhenUrl when url is invalid`() {
        val testObserver = TestObserver<Any>()

        loginWithApiActivityViewModel.isUrlValid("ftp://dot.net")
            .subscribe(testObserver)

        testObserver.assertError(InvalidUrlException::class.java)
    }

    @Test
    fun `isUrlValid when url is valid`() {
        val testObserver = TestObserver<Any>()

        loginWithApiActivityViewModel.isUrlValid("https://www.endpoint.com/")
            .subscribe(testObserver)

        verify(mockSharedPreferencesHandler).setEndpointUrl("https://www.endpoint.com/")

        testObserver.assertNoErrors()
    }

    @Test
    fun `areUsernamePasswordNotEmpty when username is empty`() {
        val testObserver = TestObserver<Any>()

        loginWithApiActivityViewModel.areUsernamePasswordNotEmpty("", "password")
            .subscribe(testObserver)

        testObserver.assertError(EmptyUsernamePasswordException::class.java)
    }

    @Test
    fun `areUsernamePasswordNotEmpty when password is empty`() {
        val testObserver = TestObserver<Boolean>()

        loginWithApiActivityViewModel.areUsernamePasswordNotEmpty("username", "")
            .subscribe(testObserver)

        testObserver.assertError(EmptyUsernamePasswordException::class.java)
    }

    @Test
    fun `areUsernamePasswordNotEmpty when username and password entered`() {
        val testObserver = TestObserver<Any>()

        loginWithApiActivityViewModel.areUsernamePasswordNotEmpty("username", "password")
            .subscribe(testObserver)

        testObserver.assertNoErrors()
    }

    private fun mockApiCall() {
        `when`(mockAuthRepository.login(AuthenticationRequest("test", "password"))).thenReturn(
            Single.just(
                AuthenticationResponse(
                    "test", "etxWWKsjakj9ajsE32X=", true,
                    mapOf("Residents" to "Read", "Incidents" to "Read")
                )
            )
        )
    }
}