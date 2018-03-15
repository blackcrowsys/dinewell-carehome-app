package com.blackcrowsys

import com.blackcrowsys.api.models.ResidentResponse
import com.blackcrowsys.persistence.entity.Resident

class MockContentHelper {
    companion object {
        fun provideListResidentsResponse(): List<ResidentResponse> {
            val residentOne = ResidentResponse(1, "Bob", "Smith", "imageUrl", "202")
            val residentTwo = ResidentResponse(2, "Ted", "Baker", "imageUrl", "202A")
            val residentThree = ResidentResponse(3, "Red", "John", "imageUrl", "203")
            return listOf(residentOne, residentTwo, residentThree)
        }

        fun provideListResidents(): List<Resident> {
            val residentOne = Resident(1, "Bob", "Smith", "imageUrl", "202")
            val residentTwo = Resident(2, "Ted", "Baker", "imageUrl", "202A")
            val residentThree = Resident(3, "Red", "John", "imageUrl", "203")
            return listOf(residentOne, residentTwo, residentThree)
        }
    }
}