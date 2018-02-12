package com.blackcrowsys.ui.pin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blackcrowsys.R
import dagger.android.AndroidInjection

const val SET_PIN_INTENT_EXTRA = "set_pin_extra"

class PINActivity : AppCompatActivity() {

    companion object {
        fun startPINActivityToSetPIN(initialContext: Context) {
            val intent = Intent(initialContext, PINActivity::class.java)
            intent.putExtra(SET_PIN_INTENT_EXTRA, true)
            initialContext.startActivity(intent)
        }

        fun startPINActivityToAuthenticatePIN(initialContext: Context) {
            val intent = Intent(initialContext, PINActivity::class.java)
            intent.putExtra(SET_PIN_INTENT_EXTRA, false)
            initialContext.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)
    }
}
