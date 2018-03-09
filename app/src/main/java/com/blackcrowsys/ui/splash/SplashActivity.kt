package com.blackcrowsys.ui.splash

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.ui.ViewModelFactory
import com.blackcrowsys.ui.login.LoginActivity
import com.blackcrowsys.ui.login.LoginWithPINActivity
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var exceptionTransformer: ExceptionTransformer

    private lateinit var viewModel: SplashActivityVewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SplashActivityVewModel::class.java)

        Thread.sleep(2500)

        compositeDisposable.add(
            viewModel.findPinHash()
                .compose(exceptionTransformer.mapExceptionsForObservable())
                .subscribeBy(onNext = {
                    LoginWithPINActivity.startLoginWithPINActivity(this)
                }, onError = {
                    LoginActivity.startLoginActivity(this)
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}
