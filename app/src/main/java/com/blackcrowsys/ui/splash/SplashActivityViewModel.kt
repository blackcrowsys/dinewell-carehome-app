package com.blackcrowsys.ui.splash

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.exceptions.NoPinHasBeenSetException
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class SplashActivityViewModel(
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

    fun findPinHash() {
        compositeDisposable.add(sharedPreferencesHandler.getPinHash()
            .flatMap { filterEmptyPinHash(it) }
            .compose(schedulerProvider.getSchedulersForObservable())
            .compose(exceptionTransformer.mapExceptionsForObservable())
            .subscribeBy(onNext = {
                viewStateResponse.value = ViewState.Success(it)
            }, onError = {
                viewStateResponse.value = ViewState.Error(it)
            })
        )
    }

    private fun filterEmptyPinHash(hash: String): Observable<String> {
        return if (hash.isBlank()) {
            Observable.error(NoPinHasBeenSetException())
        } else {
            Observable.just(hash)
        }
    }
}