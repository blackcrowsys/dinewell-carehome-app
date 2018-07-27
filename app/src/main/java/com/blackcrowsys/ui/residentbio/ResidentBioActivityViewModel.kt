package com.blackcrowsys.ui.residentbio

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.repository.ResidentRepository
import com.blackcrowsys.security.AESCipher
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class ResidentBioActivityViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val residentRepository: ResidentRepository,
    private val aesCipher: AESCipher,
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val exceptionTransformer: ExceptionTransformer
) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    var residentViewState = MutableLiveData<ViewState>()
    var residentBioViewState = MutableLiveData<ViewState>()
    var residentAllergiesViewState = MutableLiveData<ViewState>()

    override fun onCleared() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    fun retrieveResident(residentId: Int) {
        compositeDisposable.add(residentRepository.getResidentGivenId(residentId)
            .compose(schedulerProvider.getSchedulersForFlowable())
            .subscribeBy(onNext = {
                residentViewState.value = ViewState.Success(it)
            }, onError = {
                residentViewState.value = ViewState.Error(it)
            })
        )
    }

    fun retrieveResidentBio(pin: String, residentId: Int) {
        compositeDisposable.add(sharedPreferencesHandler.getEncryptedJwtToken()
            .map { aesCipher.decrypt(pin, it) }
            .flatMapSingle { residentRepository.getResidentBioFromApi(it, residentId) }
            .compose(schedulerProvider.getSchedulersForObservable())
            .compose(exceptionTransformer.mapExceptionsForObservable())
            .subscribeBy(onNext = {
                residentBioViewState.value = ViewState.Success(it)
            }, onError = {
                residentBioViewState.value = ViewState.Error(it)
            })
        )
    }

    fun retrieveResidentAllergiesFromDb(residentId: Int) {
        compositeDisposable.add(residentRepository.getResidentAllergy(residentId)
            .compose(schedulerProvider.getSchedulersForFlowable())
            .subscribeBy(onNext = {
                residentAllergiesViewState.value = ViewState.Success(it)
            }, onError = {
                residentAllergiesViewState.value = ViewState.Error(it)
            })
        )
    }
}