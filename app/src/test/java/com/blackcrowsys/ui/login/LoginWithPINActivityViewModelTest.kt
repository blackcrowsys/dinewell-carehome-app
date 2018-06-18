package com.blackcrowsys.ui.login

import android.arch.lifecycle.Observer
import com.blackcrowsys.R
import com.blackcrowsys.exceptions.ErrorMapper
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit test for [LoginWithPINActivityViewModel].
 */
@RunWith(RobolectricTestRunner::class)
class LoginWithPINActivityViewModelTest {

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    @Mock
    private lateinit var liveDataObserver: Observer<ViewState>

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var loginWithPINActivityViewModel: LoginWithPINActivityViewModel

    @Before
    fun setUp() {
        val exceptionTransformer = ExceptionTransformer(ErrorMapper(RuntimeEnvironment.application))
        MockitoAnnotations.initMocks(this)
        loginWithPINActivityViewModel =
                LoginWithPINActivityViewModel(
                    schedulerProvider,
                    mockSharedPreferencesHandler,
                    exceptionTransformer
                )

        loginWithPINActivityViewModel.authenticatedPinCheckState.observeForever(liveDataObserver)
    }

    @Test
    fun `loginWithPin when pin contains less than 4 characters`() {
        val pin = "112"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Error::class.java)

        loginWithPINActivityViewModel.loginWithPin(pin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.Error)
        assertEquals(
            RuntimeEnvironment.application.getString(R.string.pin_does_not_contain_four_digits_error),
            argumentCaptor.value.throwable.message
        )
    }

    @Test
    fun `loginWithPin when pin contains alphanumeric characters`() {
        val pin = "112A"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Error::class.java)

        loginWithPINActivityViewModel.loginWithPin(pin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.Error)
        assertEquals(
            RuntimeEnvironment.application.getString(R.string.pin_does_not_contain_four_digits_error),
            argumentCaptor.value.throwable.message
        )
    }

    @Test
    fun `loginWithPin when pin contains 4 numeric characters`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just("f1f1551102c695c3702d872fe621a054d8734210877d7e96b3635b2756637457c555254a3c40fb2d6363191628c5939050a"))
        val pin = "1121"
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Success::class.java)

        loginWithPINActivityViewModel.loginWithPin(pin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.Success)
        assertTrue(argumentCaptor.value.data as Boolean)
    }

    @Test
    fun `loginWithPin when hash does not match`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just("AKsjskjasjkmasmasd"))
        val pin = "1121"

        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Success::class.java)

        loginWithPINActivityViewModel.loginWithPin(pin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.Success)
        assertFalse(argumentCaptor.value.data as Boolean)
    }

    @Test
    fun `authenticateWithPin when hash does not exist`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just(""))
        val pin = "1121"

        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Error::class.java)

        loginWithPINActivityViewModel.loginWithPin(pin)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.Error)
        assertEquals(
            RuntimeEnvironment.application.getString(R.string.no_pin_has_been_set_error),
            argumentCaptor.value.throwable.message
        )
    }
}