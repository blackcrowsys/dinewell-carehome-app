package com.blackcrowsys.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.blackcrowsys.persistence.entity.Resident
import io.reactivex.Flowable

@Dao
interface ResidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveResident(resident: Resident)

    @Query("SELECT * FROM Resident")
    fun findAllResidents(): Flowable<List<Resident>>

    @Query("SELECT * FROM Resident WHERE firstName LIKE :firstName OR surname LIKE :surname")
    fun findResidentsGivenFirstNameSurnameSearch(
        firstName: String,
        surname: String
    ): Flowable<List<Resident>>
}