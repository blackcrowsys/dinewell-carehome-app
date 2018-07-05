package com.blackcrowsys.ui.residentbio

import android.arch.lifecycle.Observer
import android.arch.persistence.room.EmptyResultSetException
import com.blackcrowsys.MockContentHelper
import com.blackcrowsys.exceptions.ErrorMapper
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.repository.ResidentRepository
import com.blackcrowsys.security.AESCipher
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
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

@RunWith(RobolectricTestRunner::class)
class ResidentBioActivityViewModelTest {

    @Mock
    private lateinit var mockResidentRepository: ResidentRepository

    @Mock
    private lateinit var liveDataObserver: Observer<ViewState>

    @Mock
    private lateinit var mockAESCipher: AESCipher

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var residentBioActivityViewModel: ResidentBioActivityViewModel

    @Before
    fun setUp() {
        val exceptionTransformer = ExceptionTransformer(ErrorMapper(RuntimeEnvironment.application))
        MockitoAnnotations.initMocks(this)
        residentBioActivityViewModel = ResidentBioActivityViewModel(schedulerProvider,
            mockResidentRepository, mockAESCipher, mockSharedPreferencesHandler, exceptionTransformer)

        residentBioActivityViewModel.residentViewState.observeForever(liveDataObserver)
    }

    @Test
    fun `retrieveResident given valid id`() {
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Success::class.java)
        `when`(mockResidentRepository.getResidentGivenId(1)).thenReturn(Flowable.just(MockContentHelper.provideSingleResident()))

        residentBioActivityViewModel.retrieveResident(1)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())
        assertEquals(MockContentHelper.provideSingleResident(), argumentCaptor.value.data)
        assertEquals("Bob", (argumentCaptor.value.data as Resident).firstName)
    }

    @Test
    fun `retrieveResident given invalid id`() {
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Error::class.java)
        `when`(mockResidentRepository.getResidentGivenId(1)).thenReturn(Flowable.error(EmptyResultSetException("No record exists")))

        residentBioActivityViewModel.retrieveResident(1)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())
        assert(argumentCaptor.value is ViewState.Error)
    }
}