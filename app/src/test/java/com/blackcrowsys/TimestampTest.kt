package com.blackcrowsys

import org.junit.Test
import java.util.concurrent.TimeUnit

class TimestampTest {

    @Test
    fun testSixHoursInFuture() {
        val currentTimeMillis = System.currentTimeMillis()
        val newTimeMillis = currentTimeMillis + TimeUnit.HOURS.toMillis(6)
        println("Current time stamp is: $currentTimeMillis")
        println("Six Hours in future is: $newTimeMillis")

        assert(newTimeMillis > currentTimeMillis)
    }
}