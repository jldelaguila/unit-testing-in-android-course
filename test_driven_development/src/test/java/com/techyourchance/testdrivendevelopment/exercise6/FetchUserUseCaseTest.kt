package com.techyourchance.testdrivendevelopment.exercise6

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException
import com.techyourchance.testdrivendevelopment.exercise6.users.User
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class FetchUserUseCaseTest {

    companion object {
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
    }

    private val fetchUserHttpEndpointSyncMock: FetchUserHttpEndpointSync = mock()
    private val usersCacheMock: UsersCache = mock()
    private lateinit var SUT: FetchUserUseCase

    @Before
    fun setUp() {
        SUT = FetchUserUseCase(fetchUserHttpEndpointSyncMock, usersCacheMock)
        success()
        userNotCached()
    }

    @Test
    fun `WHEN fetching the user, THEN userId should be passed to the endpoint`() {
        // given
        // when
        SUT.fetchUserSync(USER_ID)
        // then
        argumentCaptor<String> {
            verify(fetchUserHttpEndpointSyncMock).fetchUserSync(capture())
            assertEquals(USER_ID, firstValue)
        }
    }

    @Test
    fun `WHEN successful, THEN user should be cached`() {
        // given
        // when
        SUT.fetchUserSync(USER_ID)
        // then
        argumentCaptor<User> {
            verify(usersCacheMock).cacheUser(capture())
            assertNotNull(firstValue)
            assertEquals(USER_ID, firstValue.userId)
            assertEquals(USER_NAME, firstValue.username)
        }
    }

    @Test
    fun `WHEN AUTH_ERROR, THEN user should be cached`() {
        // given
        authError()
        // when
        SUT.fetchUserSync(USER_ID)
        // then
        verify(usersCacheMock).getUser(anyString())
        verifyNoMoreInteractions(usersCacheMock)
    }

    @Test
    fun `WHEN GENERAL_ERROR, THEN user should not be cached`() {
        // given
        generalError()
        // when
        SUT.fetchUserSync(USER_ID)
        // then
        verify(usersCacheMock).getUser(anyString())
        verifyNoMoreInteractions(usersCacheMock)
    }

    @Test
    fun `WHEN NETWORK_ERROR, THEN user should be cached`() {
        // given
        networkError()
        // when
        SUT.fetchUserSync(USER_ID)
        // then
        verify(usersCacheMock).getUser(anyString())
        verifyNoMoreInteractions(usersCacheMock)
    }

    @Test
    fun `WHEN fetch was successful, THEN SUCCESS to be returned`() {
        // given
        // when
        val result = SUT.fetchUserSync(USER_ID)
        // then
        assertEquals(Status.SUCCESS, result.status)
    }

    @Test
    fun `WHEN AUTH_ERROR, THEN FAILURE to be returned`() {
        // given
        authError()
        // when
        val result = SUT.fetchUserSync(USER_ID)
        // then
        assertEquals(Status.FAILURE, result.status)
    }

    @Test
    fun `WHEN GENERAL_ERROR, THEN FAILURE to be returned`() {
        // given
        generalError()
        // when
        val result = SUT.fetchUserSync(USER_ID)
        // then
        assertEquals(Status.FAILURE, result.status)
    }

    @Test
    fun `WHEN NETWORK_ERROR, THEN FAILURE to be returned`() {
        // given
        networkError()
        // when
        val result = SUT.fetchUserSync(USER_ID)
        // then
        assertEquals(Status.NETWORK_ERROR, result.status)
    }

    @Test
    fun `WHEN User is cached, THEN endpoint not polled`() {
        // given
        userIsCached()
        // when
        SUT.fetchUserSync(USER_ID)
        // then
        argumentCaptor<String> {
            verify(usersCacheMock).getUser(capture())
            assertEquals(USER_ID, firstValue)
            verifyZeroInteractions(fetchUserHttpEndpointSyncMock)
        }
    }

    //region Helper methods
    private fun success() {
        whenever(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString()))
                .thenReturn(EndpointResult(EndpointStatus.SUCCESS, USER_ID, USER_NAME))
    }

    private fun generalError() {
        whenever(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString()))
                .thenReturn(EndpointResult(EndpointStatus.GENERAL_ERROR, "", ""))
    }

    private fun authError() {
        whenever(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString()))
                .thenReturn(EndpointResult(EndpointStatus.AUTH_ERROR, "", ""))
    }

    private fun networkError() {
        whenever(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString()))
                .thenThrow(NetworkErrorException())
    }

    private fun userNotCached() {
        whenever(usersCacheMock.getUser(anyString()))
                .thenReturn(null)
    }

    private fun userIsCached() {
        whenever(usersCacheMock.getUser(anyString()))
                .thenReturn(User(USER_ID, USER_NAME))
    }
    //endregion
}
