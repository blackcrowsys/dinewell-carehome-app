package com.blackcrowsys.ui.login

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.api.models.AuthenticationResponse
import com.blackcrowsys.exceptions.EmptyUsernamePasswordException
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.exceptions.InvalidUrlException
import com.blackcrowsys.repository.AuthRepository
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class LoginWithApiActivityViewModel(
    private val authRepository: AuthRepository,
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val exceptionTransformer: ExceptionTransformer
) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    var viewStateResponse = MutableLiveData<ViewState>()

    override fun onCleared() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    fun loginWithApi(username: String, password: String, url: String) {
        compositeDisposable.add(
            areUsernamePasswordNotEmpty(username, password)
                .andThen(isUrlValid(url))
                .andThen(Single.defer {
                    authenticateWithApi(AuthenticationRequest(username, password))
                })
                .compose(exceptionTransformer.mapExceptionsForSingle())
                .subscribeBy(onSuccess = {
                    viewStateResponse.value = ViewState.Success(it.jwtToken)
                }, onError = {
                    viewStateResponse.value = ViewState.Error(it)
                })
        )
    }

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
        authRepository.login(authenticationRequest)
            .compose(schedulerProvider.getSchedulersForSingle())
}