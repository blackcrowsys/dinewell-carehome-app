package com.blackcrowsys.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.blackcrowsys.persistence.entity.Allergy
import io.reactivex.Flowable

@Dao
interface AllergyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllergy(allergy: Allergy)

    @Query("SELECT * FROM Allergy")
    fun findAllAllergies(): Flowable<List<Allergy>>
}