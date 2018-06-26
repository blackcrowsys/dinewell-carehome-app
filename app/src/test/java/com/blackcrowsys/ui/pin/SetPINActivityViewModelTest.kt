package com.blackcrowsys.ui.pin

import android.arch.lifecycle.Observer
import com.blackcrowsys.R
import com.blackcrowsys.exceptions.ErrorMapper
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.functionextensions.hashString
import com.blackcrowsys.security.AESCipher
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit test for [SetPINActivityViewModel].
 */
@RunWith(RobolectricTestRunner::class)
class SetPINActivityViewModelTest {

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    @Mock
    private lateinit var mockAESCipher: AESCipher

    @Mock
    private lateinit var liveDataObserver: Observer<ViewState>

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var setPinActivityViewModel: SetPINActivityViewModel

    @Before
    fun setUp() {
        val exceptionTransformer = ExceptionTransformer(ErrorMapper(RuntimeEnvironment.application))
        MockitoAnnotations.initMocks(this)
        setPinActivityViewModel =
                SetPINActivityViewModel(
                    schedulerProvider,
                    mockSharedPreferencesHandler,
                    mockAESCipher,
                    exceptionTransformer
                )

        setPinActivityViewModel.validateFirstPinState.observeForever(liveDataObserver)
        setPinActivityViewModel.validateSecondPinState.observeForever(liveDataObserver)
        setPinActivityViewModel.encryptedJwtTokenState.observeForever(liveDataObserver)
    }

    @Test
    fun `validateFirstPin given valid PIN`() {
        val pin = "1203"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.SuccessWithNoData::class.java)

        setPinActivityViewModel.validateFirstPin(pin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.SuccessWithNoData)
    }

    @Test
    fun `validateFirstPin given PIN with same chars`() {
        val pin = "1111"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Error::class.java)

        setPinActivityViewModel.validateFirstPin(pin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.Error)
        assertEquals(
            RuntimeEnvironment.application.getString(R.string.pin_contains_same_characters_error),
            argumentCaptor.value.throwable.message
        )
    }

    @Test
    fun `validateSecondPin given same PIN`() {
        val originalPin = "1211"
        val confirmedPin = "1211"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.SuccessWithNoData::class.java)

        setPinActivityViewModel.validateSecondPin(originalPin, confirmedPin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.SuccessWithNoData)
    }

    @Test
    fun `validateSecondPin given different PIN`() {
        val originalPin = "1211"
        val confirmedPin = "1212"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Error::class.java)

        setPinActivityViewModel.validateSecondPin(originalPin, confirmedPin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.Error)
        assertEquals(
            RuntimeEnvironment.application.getString(R.string.confirmed_pin_does_not_match_error),
            argumentCaptor.value.throwable.message
        )
    }

    @Test
    fun `savePinHashAndEncryptJwt given pin and jwt`() {
        val pin = "1121"
        val jwt = "JWT Aj21n3nmkk123006534masdnLKAjd921a3eBd"
        val encryptedJwtToken = "zAAjkjsd9012jiA!odkas"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Success::class.java)

        doNothing().`when`(mockSharedPreferencesHandler).setPinHash(pin.hashString())
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just(pin.hashString()))

        `when`(mockAESCipher.encrypt(pin, jwt)).thenReturn(encryptedJwtToken)
        doNothing().`when`(mockSharedPreferencesHandler).setEncryptedJwtToken(encryptedJwtToken)
        `when`(mockSharedPreferencesHandler.getEncryptedJwtToken()).thenReturn(
            Observable.just(
                encryptedJwtToken
            )
        )

        setPinActivityViewModel.savePinHashAndEncryptJwt(pin, jwt)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())
        assertEquals(encryptedJwtToken, argumentCaptor.value.data)
    }

    @Test
    fun `savePinHashAndEncryptJwt when error occurs`() {
        val pin = "1121"
        val jwt = "JWT Aj21n3nmkk123006534masdnLKAjd921a3eBd"
        val encryptedJwtToken = "zAAjkjsd9012jiA!odkas"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Error::class.java)

        doNothing().`when`(mockSharedPreferencesHandler).setPinHash(pin.hashString())
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just(pin.hashString()))

        `when`(mockAESCipher.encrypt(pin, jwt)).thenThrow(RuntimeException::class.java)
        doNothing().`when`(mockSharedPreferencesHandler).setEncryptedJwtToken(encryptedJwtToken)
        `when`(mockSharedPreferencesHandler.getEncryptedJwtToken()).thenReturn(
            Observable.just(
                encryptedJwtToken
            )
        )

        setPinActivityViewModel.savePinHashAndEncryptJwt(pin, jwt)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())
        assert(argumentCaptor.value is ViewState.Error)
    }
}