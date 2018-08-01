package com.blackcrowsys.persistence

import android.arch.persistence.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): Date {
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return df.parse(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromDate(value: Date): String {
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return df.format(value)
    }
}