package com.blackcrowsys.security

import android.util.Base64
import android.util.Log
import com.blackcrowsys.exceptions.UnableToDecryptTokenException
import com.blackcrowsys.exceptions.UnableToEncryptTokenException
import java.nio.charset.Charset
import java.security.Key
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

const val PAD: String = "010003000404"

/**
 * Handles encryption of sensitive data.
 */
@Singleton
class AESCipher @Inject constructor() {

    fun encrypt(pin: String, valueToEnc: String): String {
        val paddedPin = pin.plus(PAD)
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)

        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(
                Cipher.ENCRYPT_MODE,
                generateKey(paddedPin),
                IvParameterSpec(iv)
            )
            val cipherText = cipher.doFinal(valueToEnc.toByteArray(Charsets.UTF_8))
            val ivAndCipherText = getCombinedArray(iv, cipherText)
            return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP)
        } catch (e: java.lang.Exception) {
            Log.e("AESCipher", "Unable to encrypt the string $valueToEnc. ${e.message}")
            throw UnableToEncryptTokenException()
        }
    }

    fun decrypt(pin: String, encryptedValue: String): String {
        val paddedPin = pin.plus(PAD)
        val ivAndCipherText = Base64.decode(encryptedValue, Base64.NO_WRAP)
        val iv = Arrays.copyOfRange(ivAndCipherText, 0, 16)
        val cipherText = Arrays.copyOfRange(ivAndCipherText, 16, ivAndCipherText.size)

        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(
                Cipher.DECRYPT_MODE,
                generateKey(paddedPin),
                IvParameterSpec(iv)
            )
            return String(cipher.doFinal(cipherText), Charsets.UTF_8)
        } catch (e: java.lang.Exception) {
            Log.e("AESCipher", "Unable to decrypt the string $encryptedValue. ${e.message}")
            throw UnableToDecryptTokenException()
        }
    }

    private fun getCombinedArray(one: ByteArray, two: ByteArray): ByteArray {
        val combined = ByteArray(one.size + two.size)
        for (i in combined.indices) {
            combined[i] = if (i < one.size) one[i] else two[i - one.size]
        }
        return combined
    }

    @Throws(Exception::class)
    private fun generateKey(paddedPin: String): Key {
        return SecretKeySpec(paddedPin.toByteArray(Charset.defaultCharset()), "AES")
    }
}