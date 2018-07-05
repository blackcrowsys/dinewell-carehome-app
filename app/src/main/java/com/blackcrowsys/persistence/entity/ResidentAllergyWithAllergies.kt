package com.blackcrowsys.persistence.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

data class ResidentAllergyWithAllergies(
    @Embedded
    var residentAllergy: ResidentAllergy = ResidentAllergy(0, 0, "Unknown"),
    @Relation(parentColumn = "allergyId", entityColumn = "allergenId")
    var allergies: List<Allergy> = emptyList()
)