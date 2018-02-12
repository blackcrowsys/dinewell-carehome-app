package com.blackcrowsys.di

import com.blackcrowsys.ui.login.LoginActivity
import com.blackcrowsys.ui.login.di.LoginActivityModule
import com.blackcrowsys.ui.pin.PINActivity
import com.blackcrowsys.ui.pin.di.PINActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(LoginActivityModule::class)])
    abstract fun bindMainActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [(PINActivityModule::class)])
    abstract fun bindPINActivity(): PINActivity
}