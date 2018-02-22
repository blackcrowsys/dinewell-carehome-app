package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blackcrowsys.R
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.functionextensions.showShortToastText
import com.blackcrowsys.ui.ViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_login_with_pin.*
import javax.inject.Inject

class LoginWithPINActivity : AppCompatActivity() {

    companion object {
        fun startLoginWithPINActivity(initialContext: Context) {
            val intent = Intent(initialContext, LoginWithPINActivity::class.java)
            initialContext.startActivity(intent)
        }
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var exceptionTransformer: ExceptionTransformer

    private lateinit var viewModel: LoginWithPINActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_pin)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(LoginWithPINActivityViewModel::class.java)

        btnLogin.setOnClickListener {
            compositeDisposable.add(viewModel.validatePin(pinView.value)
                .flatMapObservable { viewModel.authenticateWithPin(it) }
                .compose(exceptionTransformer.mapExceptionsForObservable())
                .subscribeBy(onNext = {
                    if (it) {
                        Log.d("LoginWithPINActivity", "Login success")
                    } else {
                        showShortToastText(getString(R.string.pin_does_not_match))
                    }
                }, onError = {
                    Log.e("LoginWithPINActivity", "Error ${it.message}")
                    showShortToastText(it.message)
                })
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}
