package com.blackcrowsys.repository

import com.blackcrowsys.api.ApiService
import com.blackcrowsys.persistence.dao.ResidentDao
import com.blackcrowsys.persistence.entity.Resident
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResidentRepository @Inject constructor(
    private val apiService: ApiService,
    private val residentDao: ResidentDao
) {

    fun getResidentsFromApi(): Single<List<Resident>> {
        return apiService.getResidents()
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { it ->
                residentDao.saveResident(Resident(it))
                Single.just(Resident(it))
            }
            .toList()
    }

    fun getResidentsFromCache(): Flowable<List<Resident>> = residentDao.findAllResidents()
}