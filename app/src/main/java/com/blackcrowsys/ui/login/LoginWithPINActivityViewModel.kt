package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.NoPinHasBeenSetException
import com.blackcrowsys.exceptions.PinDoesNotContainFourDigitsException
import com.blackcrowsys.functionextensions.hashString
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import io.reactivex.Observable
import io.reactivex.Single

class LoginWithPINActivityViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferencesHandler: SharedPreferencesHandler
) : ViewModel() {

    fun validatePin(pin: String): Single<String> {
        return if (pin.length == 4 && pin.matches(Regex("[0-9]+"))) {
            Single.just(pin)
        } else {
            Single.error(PinDoesNotContainFourDigitsException())
        }
    }

    fun authenticateWithPin(pin: String): Observable<Boolean> {
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