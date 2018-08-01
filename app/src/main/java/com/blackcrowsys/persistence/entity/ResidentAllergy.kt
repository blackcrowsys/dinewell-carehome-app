package com.blackcrowsys.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(
    tableName = "ResidentAllergy",
    primaryKeys = ["residentId", "allergyId"],
    foreignKeys = [
        ForeignKey(
            entity = Resident::class,
            parentColumns = ["residentId"],
            childColumns = ["residentId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Allergy::class,
            parentColumns = ["allergenId"],
            childColumns = ["allergyId"],
            onDelete = CASCADE
        )
    ]
)
data class ResidentAllergy(
    val residentId: Int,
    val allergyId: Int,
    val severity: String
)