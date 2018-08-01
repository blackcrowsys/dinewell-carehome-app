package com.blackcrowsys.persistence.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.blackcrowsys.ContentHelper
import com.blackcrowsys.persistence.CareHomeDatabase
import com.blackcrowsys.persistence.entity.Incident
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IncidentDaoTest {

    private lateinit var incidentDao: IncidentDao
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
        incidentDao = db.incidentDao()
        residentDao = db.residentDao()
    }

    @After
    fun tearDown() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun createIncidentAndCheckFindAll() {
        val testSubscriber = TestSubscriber<List<Incident>>()
        residentDao.saveResident(ContentHelper.createResident())

        ContentHelper.createIncidentsForResident().forEach {
            incidentDao.saveIncident(it)
        }
        incidentDao.findAllIncidentsForResident(1)
            .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()

        testSubscriber.assertValue {
            it.size == 2 && it.first().incidentId == 1 && it[1].incidentId == 2
        }
    }
}