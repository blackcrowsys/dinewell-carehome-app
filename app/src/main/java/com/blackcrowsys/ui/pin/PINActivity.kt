package com.blackcrowsys.ui.pin

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
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.functionextensions.hashString
import com.blackcrowsys.functionextensions.hideSoftKeyboard
import com.blackcrowsys.functionextensions.showShortToastText
import com.blackcrowsys.ui.ViewModelFactory
import dagger.android.AndroidInjection
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_pin.*
import javax.inject.Inject

const val SET_PIN_INTENT_EXTRA = "set_pin_extra"
const val JWT_TOKEN_INTENT_EXTRA = "jwt_token_extra"

class PINActivity : AppCompatActivity() {

    companion object {
        fun startPINActivityToSetPIN(initialContext: Context, jwtToken: String) {
            val intent = Intent(initialContext, PINActivity::class.java)
            intent.putExtra(SET_PIN_INTENT_EXTRA, true)
            intent.putExtra(JWT_TOKEN_INTENT_EXTRA, jwtToken)
            initialContext.startActivity(intent)
        }

        fun startPINActivityToAuthenticatePIN(initialContext: Context) {
            val intent = Intent(initialContext, PINActivity::class.java)
            intent.putExtra(SET_PIN_INTENT_EXTRA, false)
            initialContext.startActivity(intent)
        }
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var exceptionTransformer: ExceptionTransformer

    private lateinit var pinActivityViewModel: PINActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        pinActivityViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(PINActivityViewModel::class.java)

        initialUiState()

        pvFirst.setPinViewEventListener { pinView, _ ->
            compositeDisposable.add(pinActivityViewModel.validatePin(pinView.value)
                .compose(exceptionTransformer.mapExceptionsForSingle())
                .subscribeBy(onSuccess = {
                    hideFirstPinLevel()
                    showSecondPinLevel()
                    pvSecond.requestFocus()
                }, onError = {
                    showShortToastText(it.message)
                })
            )
        }

        pvSecond.setPinViewEventListener { pinView, _ ->
            compositeDisposable.add(
                pinActivityViewModel.validateSecondPin(pvFirst.value, pinView.value)
                    .compose(exceptionTransformer.mapExceptionsForSingle())
                    .subscribeBy(onSuccess = {
                        hideSoftKeyboard()
                        showCompleteFloatingButton()
                        pvSecond.clearFocus()
                    }, onError = {
                        showShortToastText(it.message)
                    })
            )
        }

        btnCloseConfirmation.setOnClickListener {
            showFirstPinLevelAgain()
        }

        btnInfo.setOnClickListener {
            SimpleTooltip.Builder(this@PINActivity)
                .anchorView(btnInfo)
                .text(R.string.pin_tooltip_text)
                .gravity(Gravity.TOP)
                .animated(true)
                .backgroundColor(ContextCompat.getColor(this@PINActivity, R.color.black))
                .arrowColor(ContextCompat.getColor(this@PINActivity, R.color.black))
                .textColor(ContextCompat.getColor(this@PINActivity, R.color.white))
                .padding(R.dimen.tooltip_padding)
                .transparentOverlay(true)
                .animationDuration(500)
                .dismissOnOutsideTouch(true)
                .dismissOnInsideTouch(true)
                .build()
                .show()
        }

        fabSavePin.setOnClickListener {
            compositeDisposable.add(
                pinActivityViewModel.savePinHash(pvSecond.value.hashString())
                    .flatMap { _ ->
                        pinActivityViewModel.saveJwtTokenUsingPin(
                            pvSecond.value,
                            intent.getStringExtra(JWT_TOKEN_INTENT_EXTRA)
                        )
                    }
                    .subscribeBy(onNext = {
                        Log.d("PINActivity", "Encrypted JWT token $it")
                    }, onError = {
                        Log.e("PINActivity", "Error ${it.message}")
                    })
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
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
