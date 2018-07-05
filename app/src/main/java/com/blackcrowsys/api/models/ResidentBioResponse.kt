package com.blackcrowsys.api.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class ResidentBioResponse(
    @SerializedName("residentId")
    val residentId: Int,
    @SerializedName("firstName")
    val firstName: String,
    val surname: String,
    val allergies: List<ResidentAllergyResponse>,
    val incidents: List<IncidentResponse>,
    @SerializedName("mealHistory")
    val mealHistory: List<MealBookingItemResponse>
)

data class ResidentAllergyResponse(
    @SerializedName("allergenId")
    val allergenId: Int,
    val allergen: String,
    val severity: String
)

data class IncidentResponse(
    @SerializedName("incidentId")
    val incidentId: Int,
    val type: String,
    val description: String,
    val priority: String,
    val date: Date
)

data class MealBookingItemResponse(
    @SerializedName("mealBookingId")
    val mealBookingId: Int,
    @SerializedName("meal")
    val mealName: String,
    val type: String,
    @SerializedName("percentageCompleted")
    val percentageCompleted: Int,
    val comments: String,
    val date: Date
)