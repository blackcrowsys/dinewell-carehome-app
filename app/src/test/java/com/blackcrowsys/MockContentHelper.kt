package com.blackcrowsys

import com.blackcrowsys.api.models.ResidentResponse
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.persistence.entity.ResidentAllergy

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

        fun provideDuplicateResidents(): List<Resident> {
            val residentOne = Resident(1, "Bob", "Smith", "imageUrl", "202")
            val residentTwo = Resident(1, "Bob", "Smith", "imageUrl", "202")
            return listOf(residentOne, residentTwo)
        }

        fun provideSingleResident(): Resident = Resident(1, "Bob", "Smith", "imageUrl", "202")

        fun provideResidentAllergies(): List<ResidentAllergy> {
            val residentAllergyOne = ResidentAllergy(1, 1, "Mild")
            val residentAllergyTwo = ResidentAllergy(1, 2, "Severe")
            val residentAllergyThree = ResidentAllergy(1, 4, "Very Mild")

            return listOf(residentAllergyOne, residentAllergyTwo, residentAllergyThree)
        }
    }
}