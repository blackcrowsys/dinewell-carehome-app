package com.blackcrowsys.repository

import com.blackcrowsys.MockContentHelper
import com.blackcrowsys.api.ApiService
import com.blackcrowsys.api.models.ResidentBioResponse
import com.blackcrowsys.functionextensions.toDate
import com.blackcrowsys.persistence.dao.AllergyDao
import com.blackcrowsys.persistence.dao.IncidentDao
import com.blackcrowsys.persistence.dao.ResidentAllergyDao
import com.blackcrowsys.persistence.dao.ResidentDao
import com.blackcrowsys.persistence.datamodel.ResidentAllergyHolder
import com.blackcrowsys.persistence.entity.Allergy
import com.blackcrowsys.persistence.entity.Incident
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.persistence.entity.ResidentAllergy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ResidentRepositoryTest {

    @Mock
    private lateinit var mockApiService: ApiService

    @Mock
    private lateinit var mockResidentDao: ResidentDao

    @Mock
    private lateinit var mockResidentAllergyDao: ResidentAllergyDao

    @Mock
    private lateinit var mockAllergyDao: AllergyDao

    @Mock
    private lateinit var mockIncidentDao: IncidentDao

    private lateinit var residentRepository: ResidentRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        residentRepository = ResidentRepository(mockApiService, mockResidentDao,
            mockResidentAllergyDao, mockAllergyDao, mockIncidentDao)
    }

    @Test
    fun getResidentsFromApi() {
        val jwtToken = "jwt"
        val residentResponseList = MockContentHelper.provideListResidentsResponse()
        `when`(mockApiService.getResidents(jwtToken)).thenReturn(Single.just(residentResponseList))
        doNothing().`when`(mockResidentDao).saveResident(Resident(residentResponseList[0]))
        doNothing().`when`(mockResidentDao).saveResident(Resident(residentResponseList[1]))
        doNothing().`when`(mockResidentDao).saveResident(Resident(residentResponseList[2]))

        val testObserver = TestObserver<List<Resident>>()

        residentRepository.getResidentsFromApi(jwtToken)
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

    @Test
    fun getResidentAllergy() {
        `when`(mockResidentAllergyDao.findAllResidentAllergies(1)).thenReturn(
            Flowable.just(MockContentHelper.provideResidentAllergies())
        )
        `when`(mockAllergyDao.findAllergenById(1)).thenReturn(Flowable.just(Allergy(1, "Milk")))
        `when`(mockAllergyDao.findAllergenById(2)).thenReturn(Flowable.just(Allergy(2, "Gluten")))
        `when`(mockAllergyDao.findAllergenById(4)).thenReturn(Flowable.just(Allergy(4, "Wheat")))

        val testSubscriber = TestSubscriber<ResidentAllergyHolder>()

        residentRepository.getResidentAllergy(1)
            .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(3)
        val firstResidentAllergyHolder = testSubscriber.events.first().first() as ResidentAllergyHolder
        assertEquals("Milk", firstResidentAllergyHolder.allergy.allergen)
        assertEquals("Mild", firstResidentAllergyHolder.residentAllergy.severity)

        val secondResidentAllergyHolder = testSubscriber.events.first()[1] as ResidentAllergyHolder
        assertEquals("Gluten", secondResidentAllergyHolder.allergy.allergen)
        assertEquals("Severe", secondResidentAllergyHolder.residentAllergy.severity)

        val thirdResidentAllergyHolder = testSubscriber.events.first()[2] as ResidentAllergyHolder
        assertEquals("Wheat", thirdResidentAllergyHolder.allergy.allergen)
        assertEquals("Very Mild", thirdResidentAllergyHolder.residentAllergy.severity)
    }

    @Test
    fun getResidentBioFromApi() {
        `when`(mockApiService.getResidentBio("jwtToken", 1)).thenReturn(Single.just(MockContentHelper.provideResidentBio()))
        doNothing().`when`(mockAllergyDao).saveAllergy(Allergy(1, "Gluten"))
        doNothing().`when`(mockAllergyDao).saveAllergy(Allergy(2, "Milk"))
        doNothing().`when`(mockResidentAllergyDao).saveResidentAllergy(ResidentAllergy(1, 1, "Mild"))
        doNothing().`when`(mockResidentAllergyDao).saveResidentAllergy(ResidentAllergy(1, 2, "Severe"))
        doNothing().`when`(mockIncidentDao).saveIncident(Incident(1, "Food", "Resident had an allergic reaction to the fish and chips served on this day.",
            "Low", "12/07/2018".toDate(), 1))
        doNothing().`when`(mockIncidentDao).saveIncident(Incident(2, "Family", "Resident had a family emergency today.",
            "Medium", "12/07/2018".toDate(), 1))

        val testObserver = TestObserver<ResidentBioResponse>()

        residentRepository.getResidentBioFromApi("jwtToken", 1)
            .subscribe(testObserver)

        verify(mockAllergyDao).saveAllergy(Allergy(1, "Gluten"))
        verify(mockAllergyDao).saveAllergy(Allergy(2, "Milk"))
        verify(mockResidentAllergyDao).saveResidentAllergy(ResidentAllergy(1, 1, "Mild"))
        verify(mockResidentAllergyDao).saveResidentAllergy(ResidentAllergy(1, 2, "Severe"))
        verify(mockIncidentDao).saveIncident(Incident(1, "Food", "Resident had an allergic reaction to the fish and chips served on this day.",
            "Low", "12/07/2018".toDate(), 1))
        verify(mockIncidentDao).saveIncident(Incident(2, "Family", "Resident had a family emergency today.",
            "Medium", "12/07/2018".toDate(), 1))

        testObserver.assertNoErrors()
        testObserver.assertValue { it.residentId == 1 && it.allergies.count() == 2 &&
                it.incidents.count() == 2 && it.mealHistory.count() == 2
        }
    }
}