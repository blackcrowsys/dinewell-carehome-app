package com.blackcrowsys.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.blackcrowsys.persistence.entity.UserPermission
import io.reactivex.Flowable

/**
 * DAO for [UserPermission].
 */
@Dao
interface UserPermissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUserPermission(userPermission: UserPermission)

    @Query("SELECT * FROM UserPermission WHERE application=:application LIMIT 1")
    fun findUserPermission(application: String): Flowable<UserPermission>

    @Query("SELECT * FROM UserPermission")
    fun findAllUserPermissions(): Flowable<List<UserPermission>>
}