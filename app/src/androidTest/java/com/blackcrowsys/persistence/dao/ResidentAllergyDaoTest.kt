package com.blackcrowsys.persistence.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.blackcrowsys.ContentHelper
import com.blackcrowsys.persistence.CareHomeDatabase
import com.blackcrowsys.persistence.datamodel.ResidentAllergyHolder
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResidentAllergyDaoTest {

    private lateinit var allergyDao: AllergyDao
    private lateinit var residentDao: ResidentDao
    private lateinit var residentAllergyDao: ResidentAllergyDao
    private lateinit var db: CareHomeDatabase

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context, CareHomeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        allergyDao = db.allergyDao()
        residentDao = db.residentDao()
        residentAllergyDao = db.residentAllergyDao()
    }

    @After
    fun tearDown() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun testInsertResidentAllergyAndFindForResident() {
        val testSubscriber = TestSubscriber<ResidentAllergyHolder>()

        residentDao.saveResident(ContentHelper.createResident())

        ContentHelper.createAllergies().forEach {
            allergyDao.insertAllergy(it)
        }

        ContentHelper.createResidentAllergies().forEach {
            residentAllergyDao.insertResidentAllergy(it)
        }

        residentAllergyDao.findAllResidentAllergies(1)
            .flatMapIterable { it }
            .flatMap { residentAllergy ->
                allergyDao.findAllergenById(residentAllergy.allergyId)
                    .map { ResidentAllergyHolder(it, residentAllergy) }
            }
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
}