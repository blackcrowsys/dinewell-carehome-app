package com.blackcrowsys.ui.splash

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blackcrowsys.ui.ViewModelFactory
import com.blackcrowsys.ui.login.LoginActivity
import com.blackcrowsys.ui.login.LoginWithPINActivity
import com.blackcrowsys.util.ViewState
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SplashActivityViewModel::class.java)

        Thread.sleep(2500)

        viewModel.viewStateResponse.observe(this, Observer { response ->
            processViewStateResponse(response)
        })

        viewModel.findPinHash()
    }

    private fun processViewStateResponse(response: ViewState?) {
        when (response) {
            is ViewState.Error -> LoginActivity.startLoginActivity(this)
            is ViewState.Success<*> -> LoginWithPINActivity.startLoginWithPINActivity(this)
        }
    }
}
