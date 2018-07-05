package com.blackcrowsys.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.blackcrowsys.persistence.entity.ResidentAllergy
import com.blackcrowsys.persistence.entity.ResidentAllergyWithAllergies
import io.reactivex.Flowable

@Dao
interface ResidentAllergyDao {

    @Query("SELECT * FROM ResidentAllergy WHERE residentId=:residentId")
    fun loadResidentAllergies(residentId: Int): Flowable<ResidentAllergyWithAllergies>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResidentAllergy(residentAllergy: ResidentAllergy)
}