package com.blackcrowsys

import com.blackcrowsys.persistence.entity.Allergy
import com.blackcrowsys.persistence.entity.Resident

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
    }
}