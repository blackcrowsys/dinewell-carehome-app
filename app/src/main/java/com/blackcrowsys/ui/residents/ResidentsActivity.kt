package com.blackcrowsys.ui.residents

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.blackcrowsys.R
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.ui.ViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_residents.*
import javax.inject.Inject

class ResidentsActivity : AppCompatActivity() {

    companion object {
        fun startResidentsActivity(initialContext: Context) {
            val intent = Intent(initialContext, ResidentsActivity::class.java)
            initialContext.startActivity(intent)
        }
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var exceptionTransformer: ExceptionTransformer

    private lateinit var residentsActivityViewModel: ResidentsActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_residents)

        residentsActivityViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ResidentsActivityViewModel::class.java)

        rvResidents.apply {
            layoutManager = LinearLayoutManager(this@ResidentsActivity)
        }

        compositeDisposable.add(
            residentsActivityViewModel.getLatestResidentList()
                .compose(exceptionTransformer.mapExceptionsForSingle())
                .subscribeBy(onSuccess = {
                    rvResidents.adapter = ResidentsAdapter(it)
                    pbLoading.visibility = View.GONE
                    rvResidents.visibility = View.VISIBLE
                    fabSearch.visibility = View.VISIBLE
                }, onError = {
                    Log.e("ResidentsActivity", "${it.message}")
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}
