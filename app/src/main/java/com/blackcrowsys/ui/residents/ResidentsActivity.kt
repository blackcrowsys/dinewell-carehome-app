package com.blackcrowsys.ui.residents

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import com.blackcrowsys.R
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.ui.ViewModelFactory
import com.jakewharton.rxbinding2.widget.RxSearchView
import dagger.android.AndroidInjection
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_residents.*
import java.util.concurrent.TimeUnit
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

    private val residentsAdapter = ResidentsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_residents)

        residentsActivityViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ResidentsActivityViewModel::class.java)

        rvResidents.apply {
            layoutManager = LinearLayoutManager(this@ResidentsActivity)
            adapter = residentsAdapter
        }

        btnRetryApi.setOnClickListener {
            pbLoading.visibility = View.VISIBLE
            tvErrorView.visibility = View.GONE
            btnRetryApi.visibility = View.GONE
            loadLatestData()
        }

        loadLatestData()
    }

    private fun loadLatestData() {
        compositeDisposable.add(
            residentsActivityViewModel.getLatestResidentList()
                .compose(exceptionTransformer.mapExceptionsForSingle())
                .subscribeBy(onSuccess = {
                    residentsAdapter.submitList(it)
                    pbLoading.visibility = View.GONE
                    rvResidents.visibility = View.VISIBLE
                }, onError = {
                    tvErrorView.text = it.message
                    tvErrorView.visibility = View.VISIBLE
                    pbLoading.visibility = View.GONE
                    btnRetryApi.visibility = View.VISIBLE
                })
        )
    }

    @SuppressLint("CheckResult")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.residents_activity_menu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = getString(R.string.search_for_resident_query)

        compositeDisposable.add(
            RxSearchView.queryTextChangeEvents(searchView)
                .skipInitialValue()
                .debounce(500, TimeUnit.MICROSECONDS)
                .toFlowable(BackpressureStrategy.BUFFER)
                .flatMap { residentsActivityViewModel.performLetterBasedSearch(it.queryText()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        residentsAdapter.submitList(it)
                        if (it.isEmpty()) {
                            rvResidents.visibility = View.GONE
                            tvErrorView.text = applicationContext.getString(R.string.no_results)
                            tvErrorView.visibility = View.VISIBLE
                        } else {
                            tvErrorView.visibility = View.GONE
                            rvResidents.visibility = View.VISIBLE
                        }
                    }, onError = {
                        Log.e("ResidentsActivity", it.message)
                    }
                ))

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}
