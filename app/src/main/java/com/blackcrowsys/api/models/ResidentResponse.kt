package com.blackcrowsys.api.models

import com.google.gson.annotations.SerializedName

data class ResidentResponse(
    @SerializedName("residentId")
    val residentId: Int,
    @SerializedName("firstName")
    val firstName: String,
    val surname: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    val room: String
)