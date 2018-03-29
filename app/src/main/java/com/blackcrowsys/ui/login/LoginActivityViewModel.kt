package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModel
import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import com.blackcrowsys.exceptions.EmptyUsernamePasswordException
import com.blackcrowsys.exceptions.InvalidUrlException
import com.blackcrowsys.repository.AuthRepository
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import io.reactivex.Completable
import io.reactivex.Single

class LoginActivityViewModel(
    private val authRepository: AuthRepository,
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferencesHandler: SharedPreferencesHandler
) : ViewModel() {

    fun isUrlValid(url: String): Completable {
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            sharedPreferencesHandler.setEndpointUrl(url)
            Completable.complete()
        } else Completable.error(InvalidUrlException())
    }

    fun areUsernamePasswordNotEmpty(username: String, password: String): Completable {
        return if (username.isNotBlank() && password.isNotBlank()) {
            Completable.complete()
        } else Completable.error(EmptyUsernamePasswordException())
    }

    fun authenticateWithApi(authenticationRequest: AuthenticationRequest): Single<AuthenticationResponse> =
        authRepository.login(authenticationRequest).compose(schedulerProvider.getSchedulersForSingle())
}