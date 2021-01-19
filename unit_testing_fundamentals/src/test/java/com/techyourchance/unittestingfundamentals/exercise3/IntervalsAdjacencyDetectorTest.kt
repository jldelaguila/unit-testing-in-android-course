package com.techyourchance.unittestingfundamentals.exercise3

import com.techyourchance.unittestingfundamentals.example3.Interval
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IntervalsAdjacencyDetectorTest {

    private lateinit var SUT: IntervalsAdjacencyDetector

    @Before
    fun setUp() {
        SUT = IntervalsAdjacencyDetector()
    }

    //interval a not adjacent on the left
    @Test
    fun `isAdjacent to return false when A is not adjacent on the left`() {
        val intervalA = Interval(1, 2)
        val intervalB = Interval(10, 11)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertFalse(result)
    }

    //interval A adjacent on left
    @Test
    fun `isAdjacent to return true when A is adjacent to B on the left`() {
        val intervalA = Interval(1, 2)
        val intervalB = Interval(2, 11)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertTrue(result)
    }

    //interval A overlaps on left
    @Test
    fun `isAdjacent to return false when A overlaps B on the left`() {
        val intervalA = Interval(1, 4)
        val intervalB = Interval(2, 6)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertFalse(result)
    }

    //interval A contained by B
    @Test
    fun `isAdjacent to return false when A is contained by B`() {
        val intervalA = Interval(3, 4)
        val intervalB = Interval(1, 10)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertFalse(result)
    }

    //interval A contained by B hitting start
    @Test
    fun `isAdjacent to return false when A is contained by B hitting on the left`() {
        val intervalA = Interval(3, 4)
        val intervalB = Interval(3, 10)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertFalse(result)
    }

    //interval A contained by B hitting end
    @Test
    fun `isAdjacent to return false when A is contained by B hitting on the right`() {
        val intervalA = Interval(6, 10)
        val intervalB = Interval(3, 10)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertFalse(result)
    }

    //interval A contained by B hitting both sides
    @Test
    fun `isAdjacent to return false when A is contained by B hitting both sides`() {
        val intervalA = Interval(6, 10)
        val intervalB = Interval(6, 10)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertFalse(result)
    }

    //interval A OVERLAPS on right
    @Test
    fun `isAdjacent to return false when A overlaps B on the right`() {
        val intervalA = Interval(8, 13)
        val intervalB = Interval(6, 11)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertFalse(result)
    }

    //interval a not adjacent on the right
    @Test
    fun `isAdjacent to return false when A is not adjacent on the right`() {
        val intervalA = Interval(13, 14)
        val intervalB = Interval(10, 11)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertFalse(result)
    }

    //interval A adjacent on right
    @Test
    fun `isAdjacent to return true when A is adjacent to B on the right`() {
        val intervalA = Interval(11, 14)
        val intervalB = Interval(8, 11)

        val result = SUT.isAdjacent(intervalA, intervalB)

        assertTrue(result)
    }

}