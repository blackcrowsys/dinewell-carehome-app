package com.blackcrowsys.ui.residents

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.repository.ResidentRepository
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.ViewState
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class ResidentsActivityViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val residentRepository: ResidentRepository,
    private val exceptionTransformer: ExceptionTransformer
) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    var latestResidentsListState = MutableLiveData<ViewState>()
    var residentsListBySearchState = MutableLiveData<ViewState>()

    override fun onCleared() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    fun getLatestResidentList() {
        compositeDisposable.add(
            residentRepository.getResidentsFromApi()
            .compose(schedulerProvider.getSchedulersForSingle())
                .compose(exceptionTransformer.mapExceptionsForSingle())
                .subscribeBy(onSuccess = {
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
