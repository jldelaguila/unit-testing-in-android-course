package com.techyourchance.unittestingfundamentals.exercise1

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NegativeNumberValidatorTest {

    private lateinit var SUT: NegativeNumberValidator

    @Before
    fun setUp() {
        SUT = NegativeNumberValidator()
    }

    @Test
    fun `given 1 as input, then return false`() {
        val result = SUT.isNegative(1)
        assertFalse(result)
    }

    @Test
    fun `given 0 as input, then return false`() {
        val result = SUT.isNegative(0)
        assertFalse(result)
    }

    @Test
    fun `given -1 as input, then return false`() {
        val result = SUT.isNegative(-1)
        assertTrue(result)
    }
}