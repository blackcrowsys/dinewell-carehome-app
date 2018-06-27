package com.blackcrowsys.repository

import com.blackcrowsys.MockContentHelper
import com.blackcrowsys.api.ApiService
import com.blackcrowsys.persistence.dao.ResidentDao
import com.blackcrowsys.persistence.entity.Resident
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test

import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.MockitoAnnotations

class ResidentRepositoryTest {

    @Mock
    private lateinit var mockApiService: ApiService

    @Mock
    private lateinit var mockResidentDao: ResidentDao

    private lateinit var residentRepository: ResidentRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        residentRepository = ResidentRepository(mockApiService, mockResidentDao)
    }

    @Test
    fun getResidentsFromApi() {
        val residentResponseList = MockContentHelper.provideListResidentsResponse()
        `when`(mockApiService.getResidents("jwt")).thenReturn(Single.just(residentResponseList))
        doNothing().`when`(mockResidentDao).saveResident(Resident(residentResponseList[0]))
        doNothing().`when`(mockResidentDao).saveResident(Resident(residentResponseList[1]))
        doNothing().`when`(mockResidentDao).saveResident(Resident(residentResponseList[2]))

        val testObserver = TestObserver<List<Resident>>()

        residentRepository.getResidentsFromApi("jwt")
            .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue {
            it[0].residentId == 1 && it[1].residentId == 2 && it[2].residentId == 3
        }
    }

    @Test
    fun getResidentsFromCache() {
        `when`(mockResidentDao.findAllResidents()).thenReturn(Flowable.just(MockContentHelper.provideListResidents()))

        val testSubscriber = TestSubscriber<List<Resident>>()

        residentRepository.getResidentsFromCache()
            .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue {
            it[0].residentId == 1 && it[1].residentId == 2 && it[2].residentId == 3
        }
    }
}