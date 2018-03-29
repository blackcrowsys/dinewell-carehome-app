package com.blackcrowsys.ui.residents

import com.blackcrowsys.MockContentHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResidentDiffCallbackTest {

    private val residentDiffCallback = ResidentDiffCallback()

    @Test
    fun areItemsTheSameWithSameResidentObject() {
        assertTrue(
            residentDiffCallback.areItemsTheSame(
                MockContentHelper.provideDuplicateResidents()[0],
                MockContentHelper.provideDuplicateResidents()[1]
            )
        )
    }

    @Test
    fun areContentsTheSameWithSameResidentObject() {
        assertTrue(
            residentDiffCallback.areContentsTheSame(
                MockContentHelper.provideDuplicateResidents()[0],
                MockContentHelper.provideDuplicateResidents()[1]
            )
        )
    }

    @Test
    fun areItemsTheSameWithDifferentResidentObjects() {
        assertFalse(
            residentDiffCallback.areItemsTheSame(
                MockContentHelper.provideListResidents()[0],
                MockContentHelper.provideListResidents()[1]
            )
        )
    }

    @Test
    fun areContentsTheSameWithDifferentResidentObject() {
        assertFalse(
            residentDiffCallback.areContentsTheSame(
                MockContentHelper.provideListResidents()[0],
                MockContentHelper.provideListResidents()[1]
            )
        )
    }
}