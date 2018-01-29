package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import com.blackcrowsys.api.model.IpAddress
import com.blackcrowsys.exceptions.EmptyUsernamePasswordException
import com.blackcrowsys.exceptions.InvalidUrlException
import com.blackcrowsys.repository.Repository
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler

class LoginActivityViewModel(private val repository: Repository,
                             private val schedulerProvider: SchedulerProvider,
                             private val sharedPreferencesHandler: SharedPreferencesHandler) : ViewModel() {

    fun isUrlValid(url: String): Single<Boolean> {
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            sharedPreferencesHandler.setEndpointUrl(url)
            Single.just(true)
        } else Single.error(InvalidUrlException())
    }

    fun areUsernamePasswordNotEmpty(username: String, password: String): Single<Boolean> {
        return if (username.isNotBlank() && password.isNotBlank()) {
            Single.just(true)
        } else Single.error(EmptyUsernamePasswordException())
    }

    fun showDataFromApi(): Single<IpAddress> = repository.getDataFromApi()
            .compose(schedulerProvider.getSchedulersForSingle())
}