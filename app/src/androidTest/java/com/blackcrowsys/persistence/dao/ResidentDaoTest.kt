package com.blackcrowsys.persistence.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.blackcrowsys.ContentHelper
import com.blackcrowsys.persistence.CareHomeDatabase
import com.blackcrowsys.persistence.entity.Resident
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResidentDaoTest {
    private lateinit var residentDao: ResidentDao
    private lateinit var db: CareHomeDatabase

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context, CareHomeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        residentDao = db.residentDao()
    }

    @After
    fun tearDown() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun insertResidentAndCheckExistence() {
        val testSubscriber = TestSubscriber<List<Resident>>()

        val resident = ContentHelper.createResident()
        residentDao.saveResident(resident)
        residentDao.findAllResidents().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue { it.size == 1 }
        testSubscriber.assertValue {
            it.first().firstName == "Bob" && it.first().surname == "Smith" &&
                    it.first().residentId == 1
        }
    }

    @Test
    fun insertListResidentsAndFindNameSearch() {
        val testSubscriber = TestSubscriber<List<Resident>>()

        ContentHelper.createListResidents().forEach {
            residentDao.saveResident(it)
        }

        residentDao.findResidentsGivenFirstNameSurnameSearch("%J%", "%mit%")
            .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue { it.size == 2 }
        testSubscriber.assertValue {
            it.first().firstName == "Jill" && it[1].firstName == "Jack" &&
                    it.first().surname == it[1].surname
        }
    }

    @Test
    fun insertResidentAndCheckExistenceById() {
        val testSubscriber = TestSubscriber<Resident>()

        val resident = ContentHelper.createResident()
        residentDao.saveResident(resident)
        residentDao.findResidentGivenId(1)
            .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue { it.firstName == "Bob"
                && it.surname == "Smith" && it.residentId == 1
        }
    }
}