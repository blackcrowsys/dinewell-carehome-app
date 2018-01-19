package com.blackcrowsys.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.blackcrowsys.repository.Repository
import com.blackcrowsys.ui.login.LoginActivityViewModel
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(private val repository: Repository,
                                           private val schedulerProvider: SchedulerProvider,
                                           private val sharedPreferencesHandler: SharedPreferencesHandler) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginActivityViewModel::class.java)) {
            return LoginActivityViewModel(repository, schedulerProvider, sharedPreferencesHandler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}