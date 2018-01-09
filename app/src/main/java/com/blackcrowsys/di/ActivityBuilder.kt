package com.blackcrowsys.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.blackcrowsys.ui.main.MainActivity
import com.blackcrowsys.ui.main.di.MainActivityModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    abstract fun bindMainActivity(): MainActivity
}