package com.blackcrowsys.functionextensions

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

/**
 * Android Activity function extensions.
 */
fun Activity.showShortToastText(message: String?) {
    if (message != null)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showLongToastText(message: String?) {
    if (message != null)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.hideSoftKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
}