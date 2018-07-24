package com.blackcrowsys.functionextensions

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and

/**
 * Function extensions for Strings only.
 */
fun String.hashString(): String {
    val digest = MessageDigest.getInstance("SHA-512")
    digest.reset()

    val byteData = digest.digest(this.toByteArray(Charsets.UTF_8))
    val buffer = StringBuffer()

    for (i in 0 until byteData.size) {
        buffer.append(Integer.toString((byteData[i] and 0xff.toByte()) + 0x100, 16).substring(1))
    }
    return buffer.toString()
}

fun String.containSameCharacters(): Boolean {
    return this.indices.none { this[it] != this[0] }
}

fun String.toDate(): Date {
    val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    return df.parse(this)
}
