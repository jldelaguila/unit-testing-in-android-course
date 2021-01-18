package com.techyourchance.unittestingfundamentals.exercise2

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StringDuplicatorTest {

    private lateinit var SUT: StringDuplicator

    @Before
    fun setUp() {
        SUT = StringDuplicator()
    }

    @Test
    fun `when input is empty, then return empty string`() {
        val result = SUT.duplicate( "")
        assertEquals("", result)
    }

    @Test
    fun `when input is white space, then return twice the white space character`() {
        val result = SUT.duplicate( " ")
        assertEquals("  ", result)
    }

    @Test
    fun `when input is 1 character, then return 2 characters`() {
        val result = SUT.duplicate( "a")
        assertEquals("aa", result)
    }

    @Test
    fun `when input is several characters, then return twice as much characters`() {
        val result = SUT.duplicate( "abc")
        assertEquals("abcabc", result)
    }
}