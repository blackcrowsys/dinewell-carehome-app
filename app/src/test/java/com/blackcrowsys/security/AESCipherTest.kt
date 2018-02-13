package com.blackcrowsys.security

import com.blackcrowsys.exceptions.UnableToDecryptTokenException
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit Test for [AESCipher].
 */
@RunWith(RobolectricTestRunner::class)
class AESCipherTest {

    private val aesCipher = AESCipher()

    @Test
    fun encrypt() {
        assertNotEquals("test", aesCipher.encrypt("1111", "test"))
    }

    @Test
    fun decryptWithCorrectPIN() {
        val decryptedValue = aesCipher.decrypt("1111", "QfQntUFMUfBMFaLtB6CZuoC+95gogm9OZDbfKadGGN8=")
        assertEquals("test", decryptedValue)
    }

    @Test(expected = UnableToDecryptTokenException::class)
    fun decryptWithIncorrectPIN() {
        aesCipher.decrypt("1112", "QfQntUFMUfBMFaLtB6CZuoC+95gogm9OZDbfKadGGN8=")
    }
}