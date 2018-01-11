package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import com.blackcrowsys.api.model.IpAddress
import com.blackcrowsys.repository.Repository
import com.blackcrowsys.util.SchedulerProvider

class LoginActivityViewModel(private val repository: Repository, private val schedulerProvider: SchedulerProvider) : ViewModel() {

    fun showDataFromApi(): Single<IpAddress> = repository.getDataFromApi()
            .compose(schedulerProvider.getSchedulersForSingle())
}