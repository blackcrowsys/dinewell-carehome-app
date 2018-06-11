package com.blackcrowsys.ui.pin

import com.blackcrowsys.exceptions.ConfirmedPinDoesNotMatchException
import com.blackcrowsys.exceptions.PinContainsSameCharactersException
import com.blackcrowsys.security.AESCipher
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

class SetPINActivityViewModelTest {

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    @Mock
    private lateinit var mockAESCipher: AESCipher

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var setPinActivityViewModel: SetPINActivityViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        setPinActivityViewModel =
                SetPINActivityViewModel(
                    schedulerProvider,
                    mockSharedPreferencesHandler,
                    mockAESCipher
                )
    }

    @Test
    fun `validatePin given valid PIN`() {
        val pin = "1203"

        val testObserver = TestObserver<Any>()

        setPinActivityViewModel.validateFirstPin(pin)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
    }

    @Test
    fun `validatePin given PIN with same chars`() {
        val pin = "1111"

        val testObserver = TestObserver<Any>()

        setPinActivityViewModel.validateFirstPin(pin)
            .subscribe(testObserver)

        testObserver.assertError(PinContainsSameCharactersException::class.java)
    }

    @Test
    fun `validateSecondPin given same PIN`() {
        val originalPin = "1211"
        val confirmedPin = "1211"

        val testObserver = TestObserver<Any>()

        setPinActivityViewModel.validateSecondPin(originalPin, confirmedPin)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
    }

    @Test
    fun `validateSecondPin given different PIN`() {
        val originalPin = "1211"
        val confirmedPin = "1212"

        val testObserver = TestObserver<Any>()

        setPinActivityViewModel.validateSecondPin(originalPin, confirmedPin)
            .subscribe(testObserver)

        testObserver.assertError(ConfirmedPinDoesNotMatchException::class.java)
    }

    @Test
    fun `savePinHash with hash`() {
        val hashString = "cwKKSjsdAAndi9!lksk="
        doNothing().`when`(mockSharedPreferencesHandler).setPinHash(hashString)
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just(hashString))

        val testObserver = TestObserver<String>()
        setPinActivityViewModel.savePinHash(hashString)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it == hashString }
    }

    @Test
    fun `saveJwtTokenUsingPin given a PIN`() {
        val pin = "1112"
        val jwtToken = "JWT weeksja2jadjJJSkaj3jJAs09KAs="
        val encryptedJwtToken = "zAAjkjsd9012jiA!odkas"

        `when`(mockAESCipher.encrypt(pin, jwtToken)).thenReturn(encryptedJwtToken)
        doNothing().`when`(mockSharedPreferencesHandler).setEncryptedJwtToken(encryptedJwtToken)
        `when`(mockSharedPreferencesHandler.getEncryptedJwtToken()).thenReturn(
            Observable.just(
                encryptedJwtToken
            )
        )

        val testObserver = TestObserver<String>()
        setPinActivityViewModel.saveJwtTokenUsingPin(pin, jwtToken)
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it == encryptedJwtToken }
    }
}