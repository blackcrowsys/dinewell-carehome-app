package com.blackcrowsys.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blackcrowsys.R
import com.blackcrowsys.functionextensions.showShortToastText
import com.blackcrowsys.ui.ViewModelFactory
import com.blackcrowsys.ui.residents.ResidentsActivity
import com.blackcrowsys.util.ViewState
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login_with_pin.*
import javax.inject.Inject

class LoginWithPINActivity : AppCompatActivity() {

    companion object {
        fun startLoginWithPINActivity(initialContext: Context) {
            val intent = Intent(initialContext, LoginWithPINActivity::class.java)
            initialContext.startActivity(intent)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: LoginWithPINActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_pin)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(LoginWithPINActivityViewModel::class.java)

        viewModel.authenticatedPinCheckState.observe(this, Observer {
            processState(it)
        })

        btnLogin.setOnClickListener {
            viewModel.loginWithPin(pinView.value)
        }
    }

    private fun processState(viewState: ViewState?) {
        when (viewState) {
            is ViewState.Error -> {
                Log.e("LoginWithPINActivity", "Error ${viewState.throwable.message}")
                showShortToastText(viewState.throwable.message)
            }
            is ViewState.Success<*> -> {
                if (viewState.data as Boolean) {
                    ResidentsActivity.startResidentsActivity(this)
                } else {
                    showShortToastText(getString(R.string.pin_does_not_match))
                }
            }
        }
    }
}
