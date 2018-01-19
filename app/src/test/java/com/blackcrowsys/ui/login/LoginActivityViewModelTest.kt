package com.blackcrowsys.ui.login

import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import com.blackcrowsys.api.model.IpAddress
import com.blackcrowsys.exceptions.InvalidUrlException
import com.blackcrowsys.repository.Repository
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import org.mockito.Mockito.verify

/**
 * Unit test for [LoginActivityViewModel].
 */
class LoginActivityViewModelTest {

    @Mock
    private lateinit var mockRepository: Repository

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    private val schedulerProvider = SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var loginActivityViewModel: LoginActivityViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loginActivityViewModel = LoginActivityViewModel(mockRepository, schedulerProvider, mockSharedPreferencesHandler)
    }

    @Test
    fun showDataFromApi() {
        Mockito.`when`(mockRepository.getDataFromApi()).thenReturn(Single.just(IpAddress("20.0.0.0")))

        val testObserver = TestObserver<IpAddress>()

        loginActivityViewModel.showDataFromApi()
                .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { ipAddress -> ipAddress.ip == "20.0.0.0" }
    }

    @Test
    fun isUrlValidWhenUrlIsEmpty() {
        val testObserver = TestObserver<Boolean>()

        loginActivityViewModel.isUrlValid("")
                .subscribe(testObserver)

        testObserver.assertError(InvalidUrlException::class.java)
    }

    @Test
    fun isUrlValidWhenUrlIsInvalid() {
        val testObserver = TestObserver<Boolean>()

        loginActivityViewModel.isUrlValid("ftp://dot.net")
                .subscribe(testObserver)

        testObserver.assertError(InvalidUrlException::class.java)
    }

    @Test
    fun isUrlValidWhenUrlIsValid() {
        val testObserver = TestObserver<Boolean>()

        loginActivityViewModel.isUrlValid("https://www.endpoint.com/")
                .subscribe(testObserver)

        verify(mockSharedPreferencesHandler).setEndpointUrl("https://www.endpoint.com/")

        testObserver.assertNoErrors()
        testObserver.assertValue { result -> result }
    }
}