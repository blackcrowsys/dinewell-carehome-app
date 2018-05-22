package com.blackcrowsys.ui.splash

import com.blackcrowsys.R
import com.blackcrowsys.exceptions.ErrorMapper
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit test for [SplashActivityViewModel].
 */
@RunWith(RobolectricTestRunner::class)
class SplashActivityViewModelTest {

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var splashActivityViewModel: SplashActivityViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val exceptionTransformer = ExceptionTransformer(ErrorMapper(RuntimeEnvironment.application))
        splashActivityViewModel =
                SplashActivityViewModel(
                    schedulerProvider,
                    mockSharedPreferencesHandler,
                    exceptionTransformer
                )
    }

    @Test
    fun `findPinHash when hash exists in Shared Prefs`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just("AiaskskASKjdkjA"))

        splashActivityViewModel.findPinHash()

        val viewStateSuccess =
            splashActivityViewModel.viewStateResponse.value as ViewState.Success<*>
        assertEquals(viewStateSuccess.data, "AiaskskASKjdkjA")
    }

    @Test
    fun `findPinHash when hash does not exist in Shared Prefs`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just(""))

        splashActivityViewModel.findPinHash()

        val viewStateError = splashActivityViewModel.viewStateResponse.value as ViewState.Error
        assertEquals(
            viewStateError.throwable.message,
            RuntimeEnvironment.application.getString(R.string.no_pin_has_been_set_error)
        )
    }
}