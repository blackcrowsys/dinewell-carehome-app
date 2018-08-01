package com.blackcrowsys.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.blackcrowsys.persistence.dao.*
import com.blackcrowsys.persistence.entity.*

/**
 * Database which persists user permissions.
 */
@Database(entities = [UserPermission::class, Resident::class, Allergy::class, ResidentAllergy::class, Incident::class], version = 1)
@TypeConverters(Converters::class)
abstract class CareHomeDatabase : RoomDatabase() {

    abstract fun userPermissionDao(): UserPermissionDao
    abstract fun residentDao(): ResidentDao
    abstract fun residentAllergyDao(): ResidentAllergyDao
    abstract fun allergyDao(): AllergyDao
    abstract fun incidentDao(): IncidentDao
}