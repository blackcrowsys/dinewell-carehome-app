package com.blackcrowsys.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.blackcrowsys.persistence.dao.ResidentDao
import com.blackcrowsys.persistence.dao.UserPermissionDao
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.persistence.entity.UserPermission

/**
 * Database which persists user permissions.
 */
@Database(entities = [UserPermission::class, Resident::class], version = 1)
abstract class CareHomeDatabase : RoomDatabase() {

    abstract fun userPermissionDao(): UserPermissionDao
    abstract fun residentDao(): ResidentDao
}