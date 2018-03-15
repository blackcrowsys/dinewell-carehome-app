package com.blackcrowsys.ui.residents

import com.blackcrowsys.MockContentHelper
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.repository.ResidentRepository
import com.blackcrowsys.util.SchedulerProvider
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ResidentsActivityViewModelTest {

    @Mock
    private lateinit var mockResidentRepository: ResidentRepository

    private val schedulerProvider =
        SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())
    private lateinit var residentsActivityViewModel: ResidentsActivityViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        residentsActivityViewModel =
                ResidentsActivityViewModel(schedulerProvider, mockResidentRepository)
    }

    @Test
    fun getLatestResidentList() {
        `when`(mockResidentRepository.getResidentsFromApi()).thenReturn(
            Single.just(
                MockContentHelper.provideListResidents()
            )
        )

        val testObserver = TestObserver<List<Resident>>()

        residentsActivityViewModel.getLatestResidentList()
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue {
            it[0].residentId == 1 && it[1].residentId == 2 && it[2].residentId == 3
        }
    }

    @Test
    fun getCachedResidentsList() {
        `when`(mockResidentRepository.getResidentsFromCache()).thenReturn(
            Flowable.just(
                MockContentHelper.provideListResidents()
            )
        )

        val testSubscriber = TestSubscriber<List<Resident>>()

        residentsActivityViewModel.getCachedResidentsList()
            .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue {
            it[0].residentId == 1 && it[1].residentId == 2 && it[2].residentId == 3
        }
    }
}