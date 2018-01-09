package com.blackcrowsys.ui.main

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
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity: AppCompatActivity() {

    companion object {
        fun startMainActivity(initialContext: Context) {
            val intent = Intent(initialContext, MainActivity::class.java)
            initialContext.startActivity(intent)
        }
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)

        compositeDisposable.add(mainActivityViewModel.showDataFromApi()
                .subscribeBy(onSuccess = {
                    Log.d("MainActivity", it.ip)
                }, onError = {
                    Log.d("MainActivity", it.message)
                }))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}
