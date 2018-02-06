package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blackcrowsys.R
import com.blackcrowsys.api.models.AuthenticationRequest
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.functionextensions.getFieldValue
import com.blackcrowsys.security.AESCipher
import com.blackcrowsys.ui.ViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    companion object {
        fun startLoginActivity(initialContext: Context) {
            val intent = Intent(initialContext, LoginActivity::class.java)
            initialContext.startActivity(intent)
        }
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var exceptionTransformer: ExceptionTransformer

    private lateinit var loginActivityViewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginActivityViewModel::class.java)
        //httpInterceptor.url = "http://ip.jsontest.com/"

        btnLogin.setOnClickListener { _ ->
            compositeDisposable.add(
                loginActivityViewModel.areUsernamePasswordNotEmpty(etUsernameView.getFieldValue(), etPasswordView.getFieldValue())
                    .flatMap { _ -> loginActivityViewModel.isUrlValid(etUrlView.getFieldValue()) }
                    .flatMap { _ -> loginActivityViewModel.authenticateWithApi(AuthenticationRequest(etUsernameView.getFieldValue(), etPasswordView.getFieldValue())) }
                    .compose(exceptionTransformer.mapExceptionsForSingle())
                    .subscribeBy(onSuccess = {
                        Log.d("LoginActivity", it.jwtToken)
                    }, onError = {
                        Log.d("LoginActivity", "" + it.message)
                    })
            )
        }

        val encryptor = AESCipher()
        Log.d("LoginActivity", encryptor.encrypt("1911", "test"))
        Log.d("LoginActivity", encryptor.decrypt("1911", "zOZjBkHWwQO2Dd3WlZ2HwY3tSr7ETcnVPKLnOP7Mblg="))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}
