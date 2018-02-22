package com.blackcrowsys.exceptions

import io.reactivex.Observable
import io.reactivex.Single

class ExceptionTransformer(private val errorMapper: ErrorMapper) {

    fun <T> mapExceptionsForSingle(): (Single<T>) -> Single<T> = { single ->
        single.onErrorResumeNext { throwable ->
            Single.error(
                errorMapper.transformException(
                    throwable
                )
            )
        }
    }

    fun <T> mapExceptionsForObservable(): (Observable<T>) -> Observable<T> = { observable ->
        observable.onErrorResumeNext { t: Throwable ->
            Observable.error(
                errorMapper.transformException(
                    t
                )
            )
        }
    }
}