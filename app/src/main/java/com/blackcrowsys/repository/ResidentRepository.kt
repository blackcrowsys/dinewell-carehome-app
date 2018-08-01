package com.blackcrowsys.repository

import com.blackcrowsys.api.ApiService
import com.blackcrowsys.api.models.ResidentBioResponse
import com.blackcrowsys.functionextensions.toDate
import com.blackcrowsys.persistence.dao.AllergyDao
import com.blackcrowsys.persistence.dao.IncidentDao
import com.blackcrowsys.persistence.dao.ResidentAllergyDao
import com.blackcrowsys.persistence.dao.ResidentDao
import com.blackcrowsys.persistence.datamodel.ResidentAllergyHolder
import com.blackcrowsys.persistence.entity.Allergy
import com.blackcrowsys.persistence.entity.Incident
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.persistence.entity.ResidentAllergy
import com.blackcrowsys.util.DateTimeHandler
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResidentRepository @Inject constructor(
    private val apiService: ApiService,
    private val residentDao: ResidentDao,
    private val residentAllergyDao: ResidentAllergyDao,
    private val allergyDao: AllergyDao,
    private val incidentDao: IncidentDao,
    private val dateTimeHandler: DateTimeHandler
) {

    fun getResidents(jwtToken: String): Flowable<List<Resident>> {
        val fromApi = Flowable.just(dateTimeHandler.isCacheStale(System.currentTimeMillis()))
            .filter { it }
            .flatMap { getResidentsFromApi(jwtToken) }

        val fromDb = Flowable.just(dateTimeHandler.isCacheStale(System.currentTimeMillis()))
            .filter { !it }
            .flatMap { getResidentsFromCache() }

        return Flowable.concat(fromApi, fromDb)
    }

    fun getResidentsFromApi(jwtToken: String): Flowable<List<Resident>> {
        return apiService.getResidents(jwtToken)
            .flatMapObservable {
                dateTimeHandler.setDateTimeStamp(System.currentTimeMillis())
                Observable.fromIterable(it)
            }
            .flatMap { it ->
                residentDao.saveResident(Resident(it))
                Observable.just(Resident(it))
            }
            .toList()
            .toFlowable()
    }

    fun getResidentsFromCache(): Flowable<List<Resident>> = residentDao.findAllResidents()

    fun getResidentsGivenNameQuery(firstName: String, surname: String): Flowable<List<Resident>> {
        return residentDao.findResidentsGivenFirstNameSurnameSearch("%$firstName%", "%$surname%")
    }

    fun getResidentGivenId(residentId: Int): Flowable<Resident> = residentDao.findResidentGivenId(residentId)

    /**
     * This emits each resident allergy one by one as it's a Flowable from a continuous source.
     */
    fun getResidentAllergy(residentId: Int): Flowable<ResidentAllergyHolder> {
        return residentAllergyDao.findAllResidentAllergies(residentId)
            .flatMapIterable { it }
            .flatMap { residentAllergy ->
                allergyDao.findAllergenById(residentAllergy.allergyId)
                    .map { ResidentAllergyHolder(it, residentAllergy) }
            }
    }

    fun getResidentBioFromApi(jwtToken: String, residentId: Int): Single<ResidentBioResponse> {
        return apiService.getResidentBio(jwtToken, residentId)
            .flatMap { residentBio ->
                Observable.fromIterable(residentBio.allergies)
                    .map {
                        allergyDao.saveAllergy(Allergy(it.allergenId, it.allergen))
                        residentAllergyDao.saveResidentAllergy(ResidentAllergy(residentId, it.allergenId, it.severity))
                        it
                    }
                    .toList()
                    .flatMapObservable { Observable.fromIterable(residentBio.incidents) }
                    .map {
                        incidentDao.saveIncident(Incident(it.incidentId, it.type, it.description, it.priority, it.date.toDate(), residentId))
                        it
                    }
                    .toList()
                    .flatMap { Single.just(residentBio) }
            }
    }
}