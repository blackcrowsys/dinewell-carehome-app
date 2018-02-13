package com.blackcrowsys.functionextensions

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit test for all String function extensions.
 */
class StringExtensionsKtTest {

    @Test
    fun hashString() {
        val test = "test"
        assertEquals("e260d4a7749a1ae310a923f6190772e473f8195440e0d27a15801584f8c87f67b143732304c5f9de6f57500288f", test.hashString())
    }

    @Test
    fun containsSameCharacters() {
        val test = "1111"
        assertTrue(test.containSameCharacters())
    }

    @Test
    fun `containsSameCharacters given different characters String`() {
        val test = "1113"
        assertFalse(test.containSameCharacters())
    }
}