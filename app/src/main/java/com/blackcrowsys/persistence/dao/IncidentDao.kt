package com.blackcrowsys.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.blackcrowsys.persistence.entity.Incident
import io.reactivex.Flowable

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveIncident(incident: Incident)

    @Query("SELECT * FROM Incident WHERE residentId = :residentId")
    fun findAllIncidentsForResident(residentId: Int): Flowable<List<Incident>>
}