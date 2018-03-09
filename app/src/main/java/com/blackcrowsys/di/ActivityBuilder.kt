package com.blackcrowsys.di

import com.blackcrowsys.ui.login.LoginActivity
import com.blackcrowsys.ui.login.LoginWithPINActivity
import com.blackcrowsys.ui.login.di.LoginModule
import com.blackcrowsys.ui.pin.SetPINActivity
import com.blackcrowsys.ui.pin.di.SetPINActivityModule
import com.blackcrowsys.ui.splash.SplashActivity
import com.blackcrowsys.ui.splash.di.SplashActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(SplashActivityModule::class)])
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLoginWithPINActivity(): LoginWithPINActivity

    @ContributesAndroidInjector(modules = [(SetPINActivityModule::class)])
    abstract fun bindPINActivity(): SetPINActivity
}