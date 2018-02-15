package com.blackcrowsys.ui.pin

import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.ConfirmedPinDoesNotMatchException
import com.blackcrowsys.exceptions.PinContainsSameCharactersException
import com.blackcrowsys.functionextensions.containSameCharacters
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import io.reactivex.Observable
import io.reactivex.Single

class PINActivityViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferencesHandler: SharedPreferencesHandler
) : ViewModel() {

    fun validatePin(pin: String): Single<Boolean> {
        return Single.just(pin)
            .flatMap { thePin ->
                if (thePin.containSameCharacters()) {
                    Single.error(PinContainsSameCharactersException())
                } else {
                    Single.just(true)
                }
            }
            .compose(schedulerProvider.getSchedulersForSingle())
    }

    fun validateSecondPin(originalPin: String, confirmedPin: String): Single<Boolean> {
        return if (originalPin == confirmedPin) {
            Single.just(true)
        } else {
            Single.error(ConfirmedPinDoesNotMatchException())
        }
    }

    fun savePinHash(hashString: String): Observable<String> {
        sharedPreferencesHandler.setPinHash(hashString)
        return sharedPreferencesHandler.getPinHash()
            .filter { it -> it == hashString }
            .compose(schedulerProvider.getSchedulersForObservable())
    }
}