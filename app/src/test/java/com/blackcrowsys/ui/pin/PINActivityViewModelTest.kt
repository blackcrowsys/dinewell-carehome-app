package com.blackcrowsys.ui.pin

import com.blackcrowsys.exceptions.ConfirmedPinDoesNotMatchException
import com.blackcrowsys.exceptions.PinContainsSameCharactersException
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.MockitoAnnotations

class PINActivityViewModelTest {

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var pinActivityViewModel: PINActivityViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        pinActivityViewModel = PINActivityViewModel(schedulerProvider, mockSharedPreferencesHandler)
    }

    @Test
    fun `validatePin given valid PIN`() {
        val pin = "1203"

        val testObserver = TestObserver<Boolean>()

        pinActivityViewModel.validatePin(pin)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { result -> result }
    }

    @Test
    fun `validatePin given PIN with same chars`() {
        val pin = "1111"

        val testObserver = TestObserver<Boolean>()

        pinActivityViewModel.validatePin(pin)
            .subscribe(testObserver)

        testObserver.assertError(PinContainsSameCharactersException::class.java)
    }

    @Test
    fun `validateSecondPin given same PIN`() {
        val originalPin = "1211"
        val confirmedPin = "1211"

        val testObserver = TestObserver<Boolean>()

        pinActivityViewModel.validateSecondPin(originalPin, confirmedPin)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { result -> result }
    }

    @Test
    fun `validateSecondPin given different PIN`() {
        val originalPin = "1211"
        val confirmedPin = "1212"

        val testObserver = TestObserver<Boolean>()

        pinActivityViewModel.validateSecondPin(originalPin, confirmedPin)
            .subscribe(testObserver)

        testObserver.assertError(ConfirmedPinDoesNotMatchException::class.java)
    }

    @Test
    fun `savePinHash with hash`() {
        val hashString = "cwKKSjsdAAndi9!lksk="
        doNothing().`when`(mockSharedPreferencesHandler).setPinHash(hashString)
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just(hashString))

        val testObserver = TestObserver<String>()
        pinActivityViewModel.savePinHash(hashString)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it == hashString }
    }

    @Test
    fun `savePinHash with bad hash`() {
        val hashString = "cwKKSjsdAAndi9!lksk="
        val alteredHash = "cwKKSjsdAAndi9"
        doNothing().`when`(mockSharedPreferencesHandler).setPinHash(hashString)
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just(alteredHash))

        val testObserver = TestObserver<String>()
        pinActivityViewModel.savePinHash(hashString)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertNoValues()
        testObserver.assertComplete()
    }
}