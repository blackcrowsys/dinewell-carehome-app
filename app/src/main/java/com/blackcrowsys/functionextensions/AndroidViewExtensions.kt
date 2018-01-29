package com.blackcrowsys.functionextensions

import android.widget.EditText

/**
 * Function extensions for Android View classes only.
 */
fun EditText.getFieldValue(): String {
    return this.text.toString()
}
