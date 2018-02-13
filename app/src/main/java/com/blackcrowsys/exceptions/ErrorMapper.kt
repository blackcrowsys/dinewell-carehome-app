package com.blackcrowsys.exceptions

import android.content.Context
import com.blackcrowsys.R
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class ErrorMapper(private val context: Context) {

    fun transformException(throwable: Throwable): AppException {
        return when (throwable) {
            is InvalidUrlException -> AppException(context.getString(R.string.url_invalid_error))
            is EmptyUsernamePasswordException -> AppException(context.getString(R.string.empty_username_password_error))
            is HttpException -> transformRetrofitException(throwable)
            else -> AppException(context.getString(R.string.unknown_error), throwable.message)
        }
    }

    private fun transformRetrofitException(httpException: HttpException): AppException {
        return when (httpException.code()) {
            HTTP_UNAUTHORIZED -> AppException(context.getString(R.string.incorrect_login_details))
            else -> AppException(context.getString(R.string.unknown_error))
        }
    }
}