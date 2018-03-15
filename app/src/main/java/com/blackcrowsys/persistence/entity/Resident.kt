package com.blackcrowsys.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.blackcrowsys.api.models.ResidentResponse

@Entity(tableName = "Resident")
data class Resident(
    @PrimaryKey
    val residentId: Int,
    val firstName: String,
    val surname: String,
    val imageUrl: String,
    val room: String
) {
    constructor(residentResponse: ResidentResponse) : this(
        residentResponse.residentId,
        residentResponse.firstName,
        residentResponse.surname,
        residentResponse.imageUrl,
        residentResponse.room
    )
}