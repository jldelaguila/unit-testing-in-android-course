package com.techyourchance.testdrivendevelopment.exercise7

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.Status
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FetchReputationUseCaseTest {

    companion object {
        const val TEST_REPUTATION = 10
        const val TEST_FAILURE_REPUTATION = 0
    }

    private val getReputationHttpEndpointSyncMock: GetReputationHttpEndpointSync = mock()
    private lateinit var SUT: FetchReputationUseCaseSync

    @Before
    fun setUp() {
        SUT = FetchReputationUseCase(getReputationHttpEndpointSyncMock)
        success()
    }

    @Test
    fun `WHEN SUCCESS, THEN SUCCESS returned`() {
        // given
        // when
        val result = SUT.fetchReputation()
        // then
        assertEquals(Status.SUCCESS, result.status)
    }

    @Test
    fun `WHEN endpoint succeeds, THEN reputation fetched`() {
        // given
        // when
        val result = SUT.fetchReputation()
        // then
        assertEquals(Status.SUCCESS, result.status)
        assertEquals(TEST_REPUTATION, result.reputation)
    }

    @Test
    fun `WHEN GENERAL_ERROR, THEN FAILURE returned`() {
        // given
        generalError()
        // when
        val result = SUT.fetchReputation()
        // then
        assertEquals(Status.FAILURE, result.status)
    }

    @Test
    fun `WHEN NETWORK_ERROR, THEN FAILURE returned`() {
        // given
        networkError()
        // when
        val result = SUT.fetchReputation()
        // then
        assertEquals(Status.FAILURE, result.status)
    }

    @Test
    fun `WHEN GENERAL_ERROR, THEN 0 reputation returned`() {
        // given
        generalError()
        // when
        val result = SUT.fetchReputation()
        // then
        assertEquals(TEST_FAILURE_REPUTATION, result.reputation)
    }

    @Test
    fun `WHEN NETWORK_ERROR, THEN 0 reputation returned`() {
        // given
        networkError()
        // when
        val result = SUT.fetchReputation()
        // then
        assertEquals(TEST_FAILURE_REPUTATION, result.reputation)
    }

    //region mocking methods
    private fun generalError() {
        whenever(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(EndpointResult(EndpointStatus.GENERAL_ERROR, TEST_FAILURE_REPUTATION))
    }

    private fun networkError() {
        whenever(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(EndpointResult(EndpointStatus.NETWORK_ERROR, TEST_FAILURE_REPUTATION))
    }

    private fun success() {
        whenever(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(EndpointResult(EndpointStatus.SUCCESS, TEST_REPUTATION))
    }
    //endregion
}
