package com.blackcrowsys

import com.blackcrowsys.api.models.*
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.persistence.entity.ResidentAllergy
import java.util.*

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

        fun provideResidentBio(): ResidentBioResponse {
            val residentAllergyResponse1 = ResidentAllergyResponse(1, "Gluten", "Mild")
            val residentAllergyResponse2 = ResidentAllergyResponse(2, "Milk", "Severe")

            val incidentResponse1 = IncidentResponse(1, "Food", "Resident had an allergic reaction to the fish and chips served on this day.", "Low", Date())
            val incidentResponse2 = IncidentResponse(2, "Family", "Resident had a family emergency today.", "Medium", Date())

            val mealHistoryResponse1 = MealBookingItemResponse(1, "Fish and Chips", "Lunch", 100, "Extra mushy peas", Date())
            val mealHistoryResponse2 = MealBookingItemResponse(2, "Steak pie", "Dinner", 75, "", Date())

            return ResidentBioResponse(1, "Bob", "Smith", listOf(residentAllergyResponse1, residentAllergyResponse2),
                listOf(incidentResponse1, incidentResponse2), listOf(mealHistoryResponse1, mealHistoryResponse2))
        }
    }
}