package com.blackcrowsys.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.repository.AuthRepository
import com.blackcrowsys.repository.ResidentRepository
import com.blackcrowsys.security.AESCipher
import com.blackcrowsys.ui.login.LoginWithApiActivityViewModel
import com.blackcrowsys.ui.login.LoginWithPINActivityViewModel
import com.blackcrowsys.ui.pin.SetPINActivityViewModel
import com.blackcrowsys.ui.residents.ResidentsActivityViewModel
import com.blackcrowsys.ui.splash.SplashActivityViewModel
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(
    private val authRepository: AuthRepository,
    private val residentRepository: ResidentRepository,
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val aesCipher: AESCipher,
    private val exceptionTransformer: ExceptionTransformer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginWithApiActivityViewModel::class.java) -> LoginWithApiActivityViewModel(
                authRepository,
                schedulerProvider,
                sharedPreferencesHandler,
                exceptionTransformer
            ) as T
            modelClass.isAssignableFrom(SetPINActivityViewModel::class.java) -> SetPINActivityViewModel(
                schedulerProvider,
                sharedPreferencesHandler,
                aesCipher,
                exceptionTransformer
            ) as T
            modelClass.isAssignableFrom(LoginWithPINActivityViewModel::class.java) -> LoginWithPINActivityViewModel(
                schedulerProvider,
                sharedPreferencesHandler,
                exceptionTransformer
            ) as T
            modelClass.isAssignableFrom(SplashActivityViewModel::class.java) -> SplashActivityViewModel(
                schedulerProvider,
                sharedPreferencesHandler,
                exceptionTransformer
            ) as T
            modelClass.isAssignableFrom(ResidentsActivityViewModel::class.java) -> ResidentsActivityViewModel(
                schedulerProvider,
                residentRepository,
                exceptionTransformer
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}