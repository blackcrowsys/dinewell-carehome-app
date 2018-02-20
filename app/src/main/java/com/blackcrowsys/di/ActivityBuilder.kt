package com.blackcrowsys.di

import com.blackcrowsys.ui.login.LoginActivity
import com.blackcrowsys.ui.login.di.LoginActivityModule
import com.blackcrowsys.ui.pin.SetPINActivity
import com.blackcrowsys.ui.pin.di.SetPINActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(LoginActivityModule::class)])
    abstract fun bindMainActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [(SetPINActivityModule::class)])
    abstract fun bindPINActivity(): SetPINActivity
}