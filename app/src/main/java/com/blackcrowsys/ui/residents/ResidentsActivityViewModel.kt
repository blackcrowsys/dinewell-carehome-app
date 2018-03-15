package com.blackcrowsys.ui.residents

import android.arch.lifecycle.ViewModel
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.repository.ResidentRepository
import com.blackcrowsys.util.SchedulerProvider
import io.reactivex.Flowable
import io.reactivex.Single

class ResidentsActivityViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val residentRepository: ResidentRepository
) : ViewModel() {

    fun getLatestResidentList(): Single<List<Resident>> =
        residentRepository.getResidentsFromApi()
            .compose(schedulerProvider.getSchedulersForSingle())

    fun getCachedResidentsList(): Flowable<List<Resident>> =
        residentRepository.getResidentsFromCache()
            .compose(schedulerProvider.getSchedulersForFlowable())
}