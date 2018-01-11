package com.blackcrowsys.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

import com.blackcrowsys.R
import com.blackcrowsys.ui.ViewModelFactory
import com.facebook.imagepipeline.request.ImageRequestBuilder
import dagger.android.AndroidInjection
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

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var loginActivityViewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        val imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.mipmap.dinewell_logo_blue_green).build()
//        ivDinewellImage.setImageURI(imageRequest.sourceUri)

        loginActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginActivityViewModel::class.java)

        compositeDisposable.add(loginActivityViewModel.showDataFromApi()
                .subscribeBy(onSuccess = {
                    Log.d("LoginActivity", it.ip)
                }, onError = {
                    Log.d("LoginActivity", it.message)
                }))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}
