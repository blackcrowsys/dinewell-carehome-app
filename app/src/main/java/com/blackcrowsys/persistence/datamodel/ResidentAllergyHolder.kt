package com.blackcrowsys.persistence.datamodel

import com.blackcrowsys.persistence.entity.Allergy
import com.blackcrowsys.persistence.entity.ResidentAllergy

data class ResidentAllergyHolder(val allergy: Allergy, val residentAllergy: ResidentAllergy)