package com.blackcrowsys.exceptions

import io.reactivex.Single

class ExceptionTransformer(private val errorMapper: ErrorMapper) {

    fun <T> mapExceptionsForSingle(): (Single<T>) -> Single<T> = {
        single -> single.onErrorResumeNext { throwable -> Single.error(errorMapper.transformException(throwable)) }
    }
}