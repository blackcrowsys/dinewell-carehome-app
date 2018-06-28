package com.blackcrowsys.di

import com.blackcrowsys.ui.login.LoginWithApiActivity
import com.blackcrowsys.ui.login.LoginWithPINActivity
import com.blackcrowsys.ui.login.di.LoginModule
import com.blackcrowsys.ui.pin.SetPINActivity
import com.blackcrowsys.ui.pin.di.SetPINActivityModule
import com.blackcrowsys.ui.residentbio.ResidentBioActivity
import com.blackcrowsys.ui.residentbio.di.ResidentBioModule
import com.blackcrowsys.ui.residents.ResidentsActivity
import com.blackcrowsys.ui.residents.di.ResidentsModule
import com.blackcrowsys.ui.splash.SplashActivity
import com.blackcrowsys.ui.splash.di.SplashActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(SplashActivityModule::class)])
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLoginActivity(): LoginWithApiActivity

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLoginWithPINActivity(): LoginWithPINActivity

    @ContributesAndroidInjector(modules = [(SetPINActivityModule::class)])
    abstract fun bindPINActivity(): SetPINActivity

    @ContributesAndroidInjector(modules = [(ResidentsModule::class)])
    abstract fun bindResidentsActivity(): ResidentsActivity

    @ContributesAndroidInjector(modules = [ResidentBioModule::class])
    abstract fun bindResidentBioActivity(): ResidentBioActivity
}