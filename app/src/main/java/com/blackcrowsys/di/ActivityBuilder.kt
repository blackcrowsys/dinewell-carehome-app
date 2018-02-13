package com.blackcrowsys.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.blackcrowsys.ui.login.LoginActivity
import com.blackcrowsys.ui.login.di.LoginActivityModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = arrayOf(LoginActivityModule::class))
    abstract fun bindMainActivity(): LoginActivity
}