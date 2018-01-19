package com.blackcrowsys.util

import com.f2prateek.rx.preferences2.RxSharedPreferences

class SharedPreferencesHandler(private val rxSharedPreferences: RxSharedPreferences) {

    companion object {
        const val ENDPOINT_URL_KEY = "endpoint_url_key"
    }

    fun setEndpointUrl(url: String) = rxSharedPreferences.getString(ENDPOINT_URL_KEY).set(url)

    fun getEndpointUrl(): String {
        return rxSharedPreferences.getString(ENDPOINT_URL_KEY).get()
    }
}