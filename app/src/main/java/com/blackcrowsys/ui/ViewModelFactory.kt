package com.blackcrowsys.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.blackcrowsys.repository.Repository
import com.blackcrowsys.ui.main.MainActivityViewModel
import com.blackcrowsys.util.SchedulerProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(private val repository: Repository, private val schedulerProvider: SchedulerProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(repository, schedulerProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}