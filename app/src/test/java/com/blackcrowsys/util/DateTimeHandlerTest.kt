package com.blackcrowsys.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

class DateTimeHandlerTest {

    @Mock
    private lateinit var mockSharedPreferencesHandler: SharedPreferencesHandler

    private lateinit var dateTimeHandler: DateTimeHandler

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dateTimeHandler = DateTimeHandler(mockSharedPreferencesHandler)
    }

    @Test
    fun setDateTimeStamp() {
        val now = System.currentTimeMillis()
        val nowPlusSixHours = now + TimeUnit.HOURS.toMillis(6)
        doNothing().`when`(mockSharedPreferencesHandler).setDbRefreshTimestamp(ArgumentMatchers.anyLong())

        dateTimeHandler.setDateTimeStamp(now)

        verify(mockSharedPreferencesHandler).setDbRefreshTimestamp(nowPlusSixHours)
    }

    @Test
    fun isCacheStale() {
        val nowPlusOneMinute = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)
        `when`(mockSharedPreferencesHandler.getDbRefreshTimestamp()).thenReturn(System.currentTimeMillis())

        assertTrue(dateTimeHandler.isCacheStale(nowPlusOneMinute))
    }

    @Test
    fun `isCacheStale() when now is equal to cache timestamp`() {
        val now = System.currentTimeMillis()
        `when`(mockSharedPreferencesHandler.getDbRefreshTimestamp()).thenReturn(now)

        assertFalse(dateTimeHandler.isCacheStale(now))
    }

    @Test
    fun `isCacheStale() when now is less than cache timestamp`() {
        val now = System.currentTimeMillis()
        val nowPlusOneMinute = now + TimeUnit.MINUTES.toMillis(1)
        `when`(mockSharedPreferencesHandler.getDbRefreshTimestamp()).thenReturn(nowPlusOneMinute)

        assertFalse(dateTimeHandler.isCacheStale(now))
    }
}