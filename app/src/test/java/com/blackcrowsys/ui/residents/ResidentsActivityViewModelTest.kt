package com.blackcrowsys.ui.residents

import android.arch.lifecycle.Observer
import com.blackcrowsys.MockContentHelper
import com.blackcrowsys.R
import com.blackcrowsys.exceptions.ErrorMapper
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.repository.ResidentRepository
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.ViewState
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.net.UnknownHostException

/**
 * Unit test for [ResidentsActivityViewModel].
 */
@RunWith(RobolectricTestRunner::class)
class ResidentsActivityViewModelTest {

    @Mock
    private lateinit var mockResidentRepository: ResidentRepository

    @Mock
    private lateinit var liveDataObserver: Observer<ViewState>

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())
    private lateinit var residentsActivityViewModel: ResidentsActivityViewModel

    @Before
    fun setUp() {
        val exceptionTransformer = ExceptionTransformer(ErrorMapper(RuntimeEnvironment.application))
        MockitoAnnotations.initMocks(this)
        residentsActivityViewModel =
                ResidentsActivityViewModel(
                    schedulerProvider,
                    mockResidentRepository,
                    exceptionTransformer
                )

        residentsActivityViewModel.latestResidentsListState.observeForever(liveDataObserver)
        residentsActivityViewModel.residentsListBySearchState.observeForever(liveDataObserver)
    }

    @Test
    fun getLatestResidentList() {
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Success::class.java)

        `when`(mockResidentRepository.getResidentsFromApi()).thenReturn(
            Single.just(
                MockContentHelper.provideListResidents()
            )
        )

        residentsActivityViewModel.getLatestResidentList()

        verify(liveDataObserver).onChanged(argumentCaptor.capture())
        assertEquals(MockContentHelper.provideListResidents(), argumentCaptor.value.data)
    }


    @Test
    fun `getLatestResidentList when network error`() {
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Error::class.java)

        `when`(mockResidentRepository.getResidentsFromApi()).thenReturn(
            Single.error(UnknownHostException())
        )

        residentsActivityViewModel.getLatestResidentList()

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        assert(argumentCaptor.value is ViewState.Error)
        assertEquals(
            RuntimeEnvironment.application.getString(R.string.unable_to_connect_to_be_error),
            argumentCaptor.value.throwable.message
        )
    }

    @Test
    fun performLetterBasedSearchGivenEmptySearchString() {
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Success::class.java)
        `when`(mockResidentRepository.getResidentsFromCache()).thenReturn(
            Flowable.just(
                MockContentHelper.provideListResidents()
            )
        )

        residentsActivityViewModel.performLetterBasedSearch("")

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        verify(mockResidentRepository).getResidentsFromCache()
        verify(
            mockResidentRepository,
            never()
        ).getResidentsGivenNameQuery(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())

        assertEquals(MockContentHelper.provideListResidents(), argumentCaptor.value.data)
    }

    @Test
    fun performLetterBasedSearchGivenSearchString() {
        val argumentCaptor = ArgumentCaptor.forClass(ViewState.Success::class.java)
        val searchString = "ted"
        `when`(mockResidentRepository.getResidentsGivenNameQuery(searchString, searchString))
            .thenReturn(Flowable.just(MockContentHelper.provideListResidents()))

        residentsActivityViewModel.performLetterBasedSearch(searchString)

        verify(liveDataObserver).onChanged(argumentCaptor.capture())

        verify(mockResidentRepository).getResidentsGivenNameQuery(searchString, searchString)
        verify(mockResidentRepository, never()).getResidentsFromCache()

        assertEquals(MockContentHelper.provideListResidents(), argumentCaptor.value.data)
    }
}
