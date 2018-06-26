package com.blackcrowsys.ui.pin

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.ConfirmedPinDoesNotMatchException
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.exceptions.PinContainsSameCharactersException
import com.blackcrowsys.functionextensions.containSameCharacters
import com.blackcrowsys.functionextensions.hashString
import com.blackcrowsys.security.AESCipher
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class SetPINActivityViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val aesCipher: AESCipher,
    private val exceptionTransformer: ExceptionTransformer
) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    var validateFirstPinState = MutableLiveData<ViewState>()
    var validateSecondPinState = MutableLiveData<ViewState>()
    var encryptedJwtTokenState = MutableLiveData<ViewState>()

    override fun onCleared() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    fun validateFirstPin(pin: String) {
        compositeDisposable.add(Single.just(pin)
            .flatMapCompletable {
                if (it.containSameCharacters()) {
                    Completable.error(PinContainsSameCharactersException())
                } else {
                    Completable.complete()
                }
            }
            .compose(exceptionTransformer.mapExceptionsForCompletable())
            .subscribeBy(onComplete = {
                validateFirstPinState.value = ViewState.SuccessWithNoData()
            }, onError = {
                validateFirstPinState.value = ViewState.Error(it)
            })
        )
    }

    fun validateSecondPin(originalPin: String, confirmedPin: String) {
        compositeDisposable.add(Single.just(originalPin)
            .flatMapCompletable {
                if (originalPin == confirmedPin) {
                    Completable.complete()
                } else {
                    Completable.error(ConfirmedPinDoesNotMatchException())
                }
            }
            .compose(exceptionTransformer.mapExceptionsForCompletable())
            .subscribeBy(onComplete = {
                validateSecondPinState.value = ViewState.SuccessWithNoData()
            }, onError = {
                validateSecondPinState.value = ViewState.Error(it)
            })
        )
    }

    fun savePinHashAndEncryptJwt(pin: String, jwtToken: String) {
        savePinHash(pin.hashString())
            .flatMap { saveJwtTokenUsingPin(pin, jwtToken) }
            .subscribeBy(onNext = {
                encryptedJwtTokenState.value = ViewState.Success(it)
            }, onError = {
                encryptedJwtTokenState.value = ViewState.Error(it)
            })
    }

    private fun savePinHash(hashString: String): Observable<String> {
        sharedPreferencesHandler.setPinHash(hashString)
        return sharedPreferencesHandler.getPinHash()
            .compose(schedulerProvider.getSchedulersForObservable())
    }

    private fun saveJwtTokenUsingPin(pin: String, jwtToken: String): Observable<String> {
        sharedPreferencesHandler.setEncryptedJwtToken(aesCipher.encrypt(pin, jwtToken))
        return sharedPreferencesHandler.getEncryptedJwtToken()
            .compose(schedulerProvider.getSchedulersForObservable())
    }
}