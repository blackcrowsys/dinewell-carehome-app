package com.blackcrowsys.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Allergy")
data class Allergy(
    @PrimaryKey
    val allergenId: Int,
    val allergen: String
)