package com.blackcrowsys

import com.blackcrowsys.persistence.entity.Allergy
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.persistence.entity.ResidentAllergy

class ContentHelper {
    companion object {

        fun createResident(): Resident = Resident(1, "Bob", "Smith", "", "Room 205")

        fun createListResidents(): List<Resident> {
            val residentOne = Resident(2, "Jill", "Smith", "", "Room 205")
            val residentTwo = Resident(3, "Jack", "Smith", "", "Room 206")
            val residentThree = Resident(4, "Muddy", "Waters", "", "Room 207")
            val residentFour = Resident(5, "Max", "Nike", "", "Room 209 B1")
            val residentFive = Resident(6, "Eric", "Prince", "", "Room 214 B1")

            return listOf(residentOne, residentTwo, residentThree, residentFour, residentFive)
        }

        fun createAllergy(): Allergy = Allergy(1, "Milk")

        fun createAllergies(): List<Allergy> {
            val allergyOne = Allergy(1, "Milk")
            val allergyTwo = Allergy(2, "Gluten")
            val allergyThree = Allergy(3, "Eggs")
            val allergyFour = Allergy(4, "Wheat")
            val allergyFive = Allergy(5, "Fish")

            return listOf(allergyOne, allergyTwo, allergyThree, allergyFour, allergyFive)
        }

        fun createResidentAllergies(): List<ResidentAllergy> {
            val residentAllergyOne = ResidentAllergy(1, 1, "Mild")
            val residentAllergyTwo = ResidentAllergy(1, 2, "Severe")
            val residentAllergyThree = ResidentAllergy(1, 4, "Very Mild")

            return listOf(residentAllergyOne, residentAllergyTwo, residentAllergyThree)
        }
    }
}