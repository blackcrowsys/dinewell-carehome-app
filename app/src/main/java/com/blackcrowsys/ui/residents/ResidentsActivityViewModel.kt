package com.blackcrowsys.ui.residents

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.repository.ResidentRepository
import com.blackcrowsys.security.AESCipher
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.blackcrowsys.util.ViewState
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class ResidentsActivityViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val residentRepository: ResidentRepository,
    private val aesCipher: AESCipher,
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val exceptionTransformer: ExceptionTransformer
) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    var latestResidentsListState = MutableLiveData<ViewState>()
    var residentsListBySearchState = MutableLiveData<ViewState>()

    override fun onCleared() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    fun getLatestResidentList(pin: String) {
        compositeDisposable.add(
            sharedPreferencesHandler.getEncryptedJwtToken()
                .map { aesCipher.decrypt(pin, it) }
                .flatMapSingle { residentRepository.getResidentsFromApi(it) }
                .compose(schedulerProvider.getSchedulersForObservable())
                .compose(exceptionTransformer.mapExceptionsForObservable())
                .subscribeBy(onNext = {
                    latestResidentsListState.value = ViewState.Success(it)
                }, onError = {
                    latestResidentsListState.value = ViewState.Error(it)
                })
        )
    }

    fun performLetterBasedSearch(searchString: CharSequence) {
        compositeDisposable.add(Flowable.just(searchString.toString())
            .filter { it.isNotBlank() }
            .debounce(500, TimeUnit.MILLISECONDS)
            .flatMap { residentRepository.getResidentsGivenNameQuery(it, it) }
            .compose(schedulerProvider.getSchedulersForFlowable())
            .subscribeBy(onNext = {
                residentsListBySearchState.value = ViewState.Success(it)
            }, onError = {
                residentsListBySearchState.value = ViewState.Error(it)
            })
        )

        compositeDisposable.add(Flowable.just(searchString.toString())
            .filter { it.isBlank() }
            .debounce(500, TimeUnit.MILLISECONDS)
            .flatMap { residentRepository.getResidentsFromCache() }
            .compose(schedulerProvider.getSchedulersForFlowable())
            .subscribeBy(onNext = {
                residentsListBySearchState.value = ViewState.Success(it)
            }, onError = {
                residentsListBySearchState.value = ViewState.Error(it)
            })
        )
    }
}
