package com.blackcrowsys.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.blackcrowsys.persistence.dao.UserPermissionDao
import com.blackcrowsys.persistence.entity.UserPermission

/**
 * Database which persists user permissions.
 */
@Database(entities = [(UserPermission::class)], version = 1)
abstract class UserAccessDatabase : RoomDatabase() {

    abstract fun userPermissionDao(): UserPermissionDao
}