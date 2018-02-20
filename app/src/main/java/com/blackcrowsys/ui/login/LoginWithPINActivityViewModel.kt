package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.PinDoesNotContainFourDigitsException
import com.blackcrowsys.functionextensions.hashString
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
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

    fun authenticateWithPin(pin: String): Single<Boolean> {
        return Single.fromObservable(sharedPreferencesHandler.getPinHash())
            .flatMap { Single.just(it == pin.hashString()) }
            .compose(schedulerProvider.getSchedulersForSingle())
    }

}