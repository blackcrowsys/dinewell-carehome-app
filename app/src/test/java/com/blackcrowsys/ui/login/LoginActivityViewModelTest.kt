package com.blackcrowsys.ui.login

import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import com.blackcrowsys.exceptions.EmptyUsernamePasswordException
import com.blackcrowsys.exceptions.InvalidUrlException
import com.blackcrowsys.repository.AuthRepository
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Unit test for [LoginActivityViewModel].
 */
class LoginActivityViewModelTest {

    @Mock
    private lateinit var mockAuthRepository: AuthRepository

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    private val schedulerProvider = SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var loginActivityViewModel: LoginActivityViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loginActivityViewModel = LoginActivityViewModel(
            mockAuthRepository,
            schedulerProvider,
            mockSharedPreferencesHandler
        )
    }

    @Test
    fun `authenticate With Api`() {
        `when`(mockAuthRepository.login(AuthenticationRequest("test", "password"))).thenReturn(
            Single.just(
            AuthenticationResponse("test", "etxWWKsjakj9ajsE32X=", true,
                mapOf("Residents" to "Read", "Incidents" to "Read"))
        ))

        val testObserver = TestObserver<AuthenticationResponse>()

        loginActivityViewModel.authenticateWithApi(AuthenticationRequest("test", "password"))
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it -> it.jwtToken=="etxWWKsjakj9ajsE32X=" &&
                it.permissions.containsKey("Residents") &&
                it.permissions.containsKey("Incidents")}
    }

    @Test
    fun `isUrlValid when url is empty`() {
        val testObserver = TestObserver<Any>()

        loginActivityViewModel.isUrlValid("")
                .subscribe(testObserver)

        testObserver.assertError(InvalidUrlException::class.java)
    }

    @Test
    fun `isUrlValidWhenUrl when url is invalid`() {
        val testObserver = TestObserver<Any>()

        loginActivityViewModel.isUrlValid("ftp://dot.net")
                .subscribe(testObserver)

        testObserver.assertError(InvalidUrlException::class.java)
    }

    @Test
    fun `isUrlValid when url is valid`() {
        val testObserver = TestObserver<Any>()

        loginActivityViewModel.isUrlValid("https://www.endpoint.com/")
                .subscribe(testObserver)

        verify(mockSharedPreferencesHandler).setEndpointUrl("https://www.endpoint.com/")

        testObserver.assertNoErrors()
    }

    @Test
    fun `areUsernamePasswordNotEmpty when username is empty`() {
        val testObserver = TestObserver<Any>()

        loginActivityViewModel.areUsernamePasswordNotEmpty("", "password")
            .subscribe(testObserver)

        testObserver.assertError(EmptyUsernamePasswordException::class.java)
    }

    @Test
    fun `areUsernamePasswordNotEmpty when password is empty`() {
        val testObserver = TestObserver<Boolean>()

        loginActivityViewModel.areUsernamePasswordNotEmpty("username", "")
            .subscribe(testObserver)

        testObserver.assertError(EmptyUsernamePasswordException::class.java)
    }

    @Test
    fun `areUsernamePasswordNotEmpty when username and password entered`() {
        val testObserver = TestObserver<Any>()

        loginActivityViewModel.areUsernamePasswordNotEmpty("username", "password")
            .subscribe(testObserver)

        testObserver.assertNoErrors()
    }
}