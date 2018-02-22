package com.blackcrowsys.ui.splash

import com.blackcrowsys.exceptions.NoPinHasBeenSetException
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
 * Unit test for [SplashActivityVewModel].
 */
class SplashActivityVewModelTest {

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var splashActivityVewModel: SplashActivityVewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        splashActivityVewModel =
                SplashActivityVewModel(schedulerProvider, mockSharedPreferencesHandler)
    }

    @Test
    fun `findPinHash when hash exists in Shared Prefs`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just("AiaskskASKjdkjA"))
        val testObserver = TestObserver<String>()

        splashActivityVewModel.findPinHash()
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it === "AiaskskASKjdkjA" }
    }

    @Test
    fun `findPinHash when hash does not exist in Shared Prefs`() {
        `when`(mockSharedPreferencesHandler.getPinHash()).thenReturn(Observable.just(""))
        val testObserver = TestObserver<String>()

        splashActivityVewModel.findPinHash()
            .subscribe(testObserver)

        testObserver.assertError(NoPinHasBeenSetException::class.java)
    }
}