package com.blackcrowsys.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.blackcrowsys.persistence.entity.ResidentAllergyWithAllergies
import io.reactivex.Flowable

@Dao
interface ResidentAllergyDao {

    @Query("SELECT * FROM ResidentAllergy WHERE residentId=:residentId")
    fun loadResidentAllergies(residentId: Int): Flowable<ResidentAllergyWithAllergies>
}