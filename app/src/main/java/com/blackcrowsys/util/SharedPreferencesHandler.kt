package com.blackcrowsys.util

import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Observable

const val ENDPOINT_URL_KEY = "endpoint_url_key"
const val PIN_HASH_KEY = "pin_hash_key"
const val ENCRYPTED_JWT_TOKEN_KEY = "encrypted_jwt_key"

class SharedPreferencesHandler(private val rxSharedPreferences: RxSharedPreferences) {

    fun setEndpointUrl(url: String) = rxSharedPreferences.getString(ENDPOINT_URL_KEY).set(url)

    fun getEndpointUrl(): String {
        return rxSharedPreferences.getString(ENDPOINT_URL_KEY).get()
    }

    fun setPinHash(hash: String) = rxSharedPreferences.getString(PIN_HASH_KEY).set(hash)

    fun getPinHash(): Observable<String> {
        return rxSharedPreferences.getString(PIN_HASH_KEY).asObservable()
    }

    fun setEncryptedJwtToken(encryptedJwtToken: String) =
        rxSharedPreferences.getString(ENCRYPTED_JWT_TOKEN_KEY).set(encryptedJwtToken)

    fun getEncryptedJwtToken(): Observable<String> {
        return rxSharedPreferences.getString(ENCRYPTED_JWT_TOKEN_KEY).asObservable()
    }
}