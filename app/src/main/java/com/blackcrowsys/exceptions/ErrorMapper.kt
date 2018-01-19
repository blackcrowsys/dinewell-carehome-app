package com.blackcrowsys.exceptions

import android.content.Context
import com.blackcrowsys.R

class ErrorMapper(val context: Context) {

    fun transformException(throwable: Throwable): AppException {
        return when (throwable) {
            is InvalidUrlException -> AppException(context.getString(R.string.url_invalid_error))
            else -> AppException(context.getString(R.string.unknown_error))
        }
    }
}