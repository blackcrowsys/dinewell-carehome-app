package com.blackcrowsys.ui.residents

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
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
import com.blackcrowsys.persistence.entity.Resident
import com.blackcrowsys.ui.ViewModelFactory
import com.blackcrowsys.util.Constants.PIN_EXTRA
import com.blackcrowsys.util.ViewState
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_residents.*
import javax.inject.Inject

class ResidentsActivity : AppCompatActivity() {

    companion object {
        fun startResidentsActivity(initialContext: Context, pin: String) {
            val intent = Intent(initialContext, ResidentsActivity::class.java)
            intent.putExtra(PIN_EXTRA, pin)
            initialContext.startActivity(intent)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

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
            residentsActivityViewModel.getLatestResidentList(intent.getStringExtra(PIN_EXTRA))
        }

        residentsActivityViewModel.latestResidentsListState.observe(this, Observer {
            processLatestResidentsList(it)
        })

        residentsActivityViewModel.residentsListBySearchState.observe(this, Observer {
            processResidentsListBySearch(it)
        })

        residentsActivityViewModel.getLatestResidentList(intent.getStringExtra(PIN_EXTRA))
    }

    @Suppress("UNCHECKED_CAST")
    private fun processResidentsListBySearch(viewState: ViewState?) {
        when (viewState) {
            is ViewState.Success<*> -> {
                val residentListBySearch = viewState.data as List<Resident>
                residentsAdapter.submitList(residentListBySearch)
                if (residentListBySearch.isEmpty()) {
                    rvResidents.visibility = View.GONE
                    tvErrorView.text = applicationContext.getString(R.string.no_results)
                    tvErrorView.visibility = View.VISIBLE
                } else {
                    tvErrorView.visibility = View.GONE
                    rvResidents.visibility = View.VISIBLE
                }
            }
            is ViewState.Error -> {
                Log.e("ResidentsActivity", viewState.throwable.message)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun processLatestResidentsList(viewState: ViewState?) {
        when (viewState) {
            is ViewState.Success<*> -> {
                residentsAdapter.submitList(viewState.data as List<Resident>)
                pbLoading.visibility = View.GONE
                rvResidents.visibility = View.VISIBLE
            }
            is ViewState.Error -> {
                tvErrorView.text = viewState.throwable.message
                tvErrorView.visibility = View.VISIBLE
                pbLoading.visibility = View.GONE
                btnRetryApi.visibility = View.VISIBLE
            }
        }
    }


    @SuppressLint("CheckResult")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.residents_activity_menu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = getString(R.string.search_for_resident_query)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                residentsActivityViewModel.performLetterBasedSearch(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                residentsActivityViewModel.performLetterBasedSearch(query)
                return true
            }
        })

        return true
    }
}
