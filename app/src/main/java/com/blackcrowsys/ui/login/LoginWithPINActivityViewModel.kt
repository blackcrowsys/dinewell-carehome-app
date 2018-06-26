package com.blackcrowsys.ui.login

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.exceptions.NoPinHasBeenSetException
import com.blackcrowsys.exceptions.PinDoesNotContainFourDigitsException
import com.blackcrowsys.functionextensions.hashString
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class LoginWithPINActivityViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val exceptionTransformer: ExceptionTransformer
) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    var authenticatedPinCheckState = MutableLiveData<ViewState>()

    override fun onCleared() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    fun loginWithPin(pin: String) {
        compositeDisposable.add(validatePin(pin)
            .flatMapObservable { authenticateWithPin(it) }
            .compose(exceptionTransformer.mapExceptionsForObservable())
            .subscribeBy(onNext = {
                authenticatedPinCheckState.value = ViewState.Success(it)
            }, onError = {
                authenticatedPinCheckState.value = ViewState.Error(it)
            })
        )
    }

    private fun validatePin(pin: String): Single<String> {
        return if (pin.length == 4 && pin.matches(Regex("[0-9]+"))) {
            Single.just(pin)
        } else {
            Single.error(PinDoesNotContainFourDigitsException())
        }
    }

    private fun authenticateWithPin(pin: String): Observable<Boolean> {
        return sharedPreferencesHandler.getPinHash()
            .flatMap { filterEmptyPinHashForAuthenticationCheck(it, pin) }
            .compose(schedulerProvider.getSchedulersForObservable())
    }

    private fun filterEmptyPinHashForAuthenticationCheck(
        hash: String,
        pin: String
    ): Observable<Boolean> {
        return if (hash.isBlank()) {
            Observable.error(NoPinHasBeenSetException())
        } else {
            Observable.just(hash == pin.hashString())
        }
    }
}