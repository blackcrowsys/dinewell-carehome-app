package com.blackcrowsys.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "Incident",
    foreignKeys = [
        ForeignKey(
        entity = Resident::class,
        parentColumns = ["residentId"],
        childColumns = ["residentId"],
        onDelete = CASCADE
    )]
)
data class Incident(
    @PrimaryKey
    val incidentId: Int,
    val type: String,
    val description: String,
    val priority: String,
    val date: Date,
    val residentId: Int
)