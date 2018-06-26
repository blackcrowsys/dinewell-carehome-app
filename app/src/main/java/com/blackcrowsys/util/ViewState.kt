package com.blackcrowsys.util

sealed class ViewState {
    data class Error(val throwable: Throwable) : ViewState()
    data class Success<T>(val data: T) : ViewState()
    class SuccessWithNoData : ViewState()
}