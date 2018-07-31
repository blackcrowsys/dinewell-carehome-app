package com.blackcrowsys.util

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateTimeHandler @Inject constructor(
    private val sharedPreferencesHandler: SharedPreferencesHandler
) {

    fun setDateTimeStamp(now: Long) {
        val sixHoursFuture = now + TimeUnit.HOURS.toMillis(6)
        sharedPreferencesHandler.setDbRefreshTimestamp(sixHoursFuture)
    }

    fun isCacheStale(now: Long): Boolean {
        return now > sharedPreferencesHandler.getDbRefreshTimestamp()
    }
}