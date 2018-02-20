package com.blackcrowsys.ui.login

import com.blackcrowsys.exceptions.PinDoesNotContainFourDigitsException
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Unit test for [LoginWithPINActivityViewModel].
 */
class LoginWithPINActivityViewModelTest {

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var loginWithPINActivityViewModel: LoginWithPINActivityViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loginWithPINActivityViewModel =
                LoginWithPINActivityViewModel(schedulerProvider, mockSharedPreferencesHandler)
    }

    @Test
    fun `validatePin when pin contains less than 4 characters`() {
        val testObserver = TestObserver<String>()
        val pin = "112"

        loginWithPINActivityViewModel.validatePin(pin)
            .subscribe(testObserver)

        testObserver.assertError(PinDoesNotContainFourDigitsException::class.java)
    }

    @Test
    fun `validatePin when pin contains alphanumeric characters`() {
        val testObserver = TestObserver<String>()
        val pin = "112A"

        loginWithPINActivityViewModel.validatePin(pin)
            .subscribe(testObserver)

        testObserver.assertError(PinDoesNotContainFourDigitsException::class.java)
    }

    @Test
    fun `validatePin when pin contains 4 numeric characters`() {
        val testObserver = TestObserver<String>()
        val pin = "1121"

        loginWithPINActivityViewModel.validatePin(pin)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it == pin }
    }

    @Test
    fun `authenticateWithPin when hash does not match`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just("AKsjskjasjkmasmasd"))
        val pin = "1121"
        val testObserver = TestObserver<Boolean>()

        loginWithPINActivityViewModel.authenticateWithPin(pin)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it -> !it }
    }

    @Test
    fun `authenticateWithPin when hash matches`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just("f1f1551102c695c3702d872fe621a054d8734210877d7e96b3635b2756637457c555254a3c40fb2d6363191628c5939050a"))
        val pin = "1121"
        val testObserver = TestObserver<Boolean>()

        loginWithPINActivityViewModel.authenticateWithPin(pin)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it -> it }
    }
}