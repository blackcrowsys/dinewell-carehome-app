package com.blackcrowsys.persistence.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.blackcrowsys.ContentHelper
import com.blackcrowsys.persistence.CareHomeDatabase
import com.blackcrowsys.persistence.entity.Allergy
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllergyDaoTest {

    private lateinit var allergyDao: AllergyDao
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
    }

    @After
    fun tearDown() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun insertAllergyAndTestRetrieveAll() {
        val testSubscriber = TestSubscriber<List<Allergy>>()

        val allergy = ContentHelper.createAllergy()
        allergyDao.insertAllergy(allergy)
        allergyDao.findAllAllergies().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue { it.size == 1 && it.first().allergen == "Milk" }
    }
}