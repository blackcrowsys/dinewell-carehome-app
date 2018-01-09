package com.blackcrowsys.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.blackcrowsys.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(2500)
        MainActivity.startMainActivity(this)
    }
}
