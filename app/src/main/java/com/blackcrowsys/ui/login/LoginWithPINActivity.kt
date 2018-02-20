package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blackcrowsys.R
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.ui.ViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
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

        }
    }
}
