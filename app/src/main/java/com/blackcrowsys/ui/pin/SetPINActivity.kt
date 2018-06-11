package com.blackcrowsys.ui.pin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import com.blackcrowsys.R
import com.blackcrowsys.functionextensions.hashString
import com.blackcrowsys.functionextensions.hideSoftKeyboard
import com.blackcrowsys.functionextensions.showShortToastText
import com.blackcrowsys.ui.ViewModelFactory
import com.blackcrowsys.ui.residents.ResidentsActivity
import com.blackcrowsys.util.ViewState
import dagger.android.AndroidInjection
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.synthetic.main.activity_pin.*
import javax.inject.Inject

const val JWT_TOKEN_INTENT_EXTRA = "jwt_token_extra"

class SetPINActivity : AppCompatActivity() {

    companion object {
        fun startSetPINActivity(initialContext: Context, jwtToken: String) {
            val intent = Intent(initialContext, SetPINActivity::class.java)
            intent.putExtra(JWT_TOKEN_INTENT_EXTRA, jwtToken)
            initialContext.startActivity(intent)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var setPinActivityViewModel: SetPINActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        setPinActivityViewModel =
                ViewModelProviders.of(this, viewModelFactory)
                    .get(SetPINActivityViewModel::class.java)

        initialUiState()

        setPinActivityViewModel.validateFirstPinState.observe(this, Observer {
            processFirstPinCheckEvent(it)
        })

        pvFirst.setPinViewEventListener { pinView, _ ->
            setPinActivityViewModel.validateFirstPin(pinView.value)
        }

        setPinActivityViewModel.validateSecondPinState.observe(this, Observer {
            processSecondPinCheckEvent(it)
        })

        pvSecond.setPinViewEventListener { pinView, _ ->
            setPinActivityViewModel.validateSecondPin(pvFirst.value, pinView.value)
        }

        btnCloseConfirmation.setOnClickListener {
            showFirstPinLevelAgain()
        }

        btnInfo.setOnClickListener {
            SimpleTooltip.Builder(this@SetPINActivity)
                .anchorView(btnInfo)
                .text(R.string.pin_tooltip_text)
                .gravity(Gravity.TOP)
                .animated(true)
                .backgroundColor(ContextCompat.getColor(this@SetPINActivity, R.color.black))
                .arrowColor(ContextCompat.getColor(this@SetPINActivity, R.color.black))
                .textColor(ContextCompat.getColor(this@SetPINActivity, R.color.white))
                .padding(R.dimen.tooltip_padding)
                .transparentOverlay(true)
                .animationDuration(500)
                .dismissOnOutsideTouch(true)
                .dismissOnInsideTouch(true)
                .build()
                .show()
        }

        setPinActivityViewModel.encryptedJwtTokenState.observe(this, Observer {
            processSavedPinAndEncyptedJwt(it)
        })

        fabSavePin.setOnClickListener {
            setPinActivityViewModel.savePinHashWithEncryptedJwt(
                pvSecond.value.hashString(),
                intent.getStringExtra(JWT_TOKEN_INTENT_EXTRA)
            )
        }
    }

    private fun processSavedPinAndEncyptedJwt(viewState: ViewState?) {
        when (viewState) {
            is ViewState.Success<*> -> {
                Log.d("SetPINActivity", "Encrypted JWT token ${viewState.data}")
                ResidentsActivity.startResidentsActivity(this)
            }
            is ViewState.Error -> {
                Log.e("SetPINActivity", "Error ${viewState.throwable.message}")
                showShortToastText(getString(R.string.unable_to_save_pin))
            }
        }
    }

    private fun processFirstPinCheckEvent(viewState: ViewState?) {
        when (viewState) {
            is ViewState.SuccessWithNoData -> {
                hideFirstPinLevel()
                showSecondPinLevel()
                pvSecond.requestFocus()
            }
            is ViewState.Error -> showShortToastText(viewState.throwable.message)
        }
    }

    private fun processSecondPinCheckEvent(viewState: ViewState?) {
        when (viewState) {
            is ViewState.SuccessWithNoData -> {
                hideSoftKeyboard()
                showCompleteFloatingButton()
                pvSecond.clearFocus()
            }
            is ViewState.Error -> showShortToastText(viewState.throwable.message)
        }
    }

    private fun initialUiState() {
        btnCloseConfirmation.visibility = View.GONE
        tvConfirmMessage.visibility = View.GONE
        pvSecond.visibility = View.GONE
        btnInfo.visibility = View.GONE
        fabSavePin.visibility = View.GONE
    }

    private fun hideFirstPinLevel() {
        tvEnterPinMessage.alpha = 0.1f
        pvFirst.alpha = 0.1f
    }

    private fun showFirstPinLevelAgain() {
        tvEnterPinMessage.alpha = 1.0f
        pvFirst.alpha = 1.0f
        pvFirst.clearValue()
        pvSecond.clearValue()
        initialUiState()
        pvFirst.requestPinEntryFocus()
    }

    private fun showSecondPinLevel() {
        btnCloseConfirmation.visibility = View.VISIBLE
        tvConfirmMessage.visibility = View.VISIBLE
        pvSecond.visibility = View.VISIBLE
        btnInfo.visibility = View.VISIBLE
    }

    private fun showCompleteFloatingButton() {
        fabSavePin.visibility = View.VISIBLE
    }
}
