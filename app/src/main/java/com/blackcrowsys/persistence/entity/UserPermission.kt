package com.blackcrowsys.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "UserPermission", indices = [(Index("application", unique = true))])
data class UserPermission(
    val application: String,
    val access: String
) {
    @PrimaryKey(autoGenerate = true) var id: Long? = null
}