package com.blackcrowsys.ui.splash

import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.NoPinHasBeenSetException
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import io.reactivex.Observable

class SplashActivityVewModel(
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferencesHandler: SharedPreferencesHandler
) : ViewModel() {

    fun findPinHash(): Observable<String> {
        return sharedPreferencesHandler.getPinHash()
            .flatMap { filterEmptyPinHash(it) }
            .compose(schedulerProvider.getSchedulersForObservable())
    }

    private fun filterEmptyPinHash(hash: String): Observable<String> {
        return if (hash.isBlank()) {
            Observable.error(NoPinHasBeenSetException())
        } else {
            Observable.just(hash)
        }
    }
}