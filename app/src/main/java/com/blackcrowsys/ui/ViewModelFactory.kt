package com.blackcrowsys.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.blackcrowsys.repository.Repository
import com.blackcrowsys.security.AESCipher
import com.blackcrowsys.ui.login.LoginActivityViewModel
import com.blackcrowsys.ui.login.LoginWithPINActivityViewModel
import com.blackcrowsys.ui.pin.SetPINActivityViewModel
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(private val repository: Repository,
                                           private val schedulerProvider: SchedulerProvider,
                                           private val sharedPreferencesHandler: SharedPreferencesHandler,
                                           private val aesCipher: AESCipher
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginActivityViewModel::class.java) -> LoginActivityViewModel(
                repository,
                schedulerProvider,
                sharedPreferencesHandler
            ) as T
            modelClass.isAssignableFrom(SetPINActivityViewModel::class.java) -> SetPINActivityViewModel(
                schedulerProvider,
                sharedPreferencesHandler,
                aesCipher
            ) as T
            modelClass.isAssignableFrom(LoginWithPINActivityViewModel::class.java) -> LoginWithPINActivityViewModel(
                schedulerProvider,
                sharedPreferencesHandler
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}