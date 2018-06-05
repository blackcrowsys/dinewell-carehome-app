package com.blackcrowsys.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blackcrowsys.R
import com.blackcrowsys.exceptions.AppException
import com.blackcrowsys.functionextensions.getFieldValue
import com.blackcrowsys.functionextensions.showLongToastText
import com.blackcrowsys.ui.ViewModelFactory
import com.blackcrowsys.ui.pin.SetPINActivity
import com.blackcrowsys.util.ViewState
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginWithApiActivity : AppCompatActivity() {

    companion object {
        fun startLoginActivity(initialContext: Context) {
            val intent = Intent(initialContext, LoginWithApiActivity::class.java)
            initialContext.startActivity(intent)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var loginWithApiActivityViewModel: LoginWithApiActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginWithApiActivityViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(LoginWithApiActivityViewModel::class.java)
        //httpInterceptor.url = "http://ip.jsontest.com/"

        loginWithApiActivityViewModel.viewStateResponse.observe(this, Observer {
            processState(it)
        })

        btnLogin.setOnClickListener { _ ->
            loginWithApiActivityViewModel.loginWithApi(
                etUsernameView.getFieldValue(),
                etPasswordView.getFieldValue(),
                etUrlView.getFieldValue()
            )
        }
    }

    private fun processState(viewState: ViewState?) {
        when (viewState) {
            is ViewState.Error -> {
                val appException = viewState.throwable as AppException
                Log.e(
                    "LoginActivity",
                    "${appException.message}. Cause: ${appException.secondaryMessage}"
                )
                showLongToastText(viewState.throwable.message)
            }
            is ViewState.Success<*> -> SetPINActivity.startSetPINActivity(
                this,
                viewState.data as String
            )
        }
    }
}
