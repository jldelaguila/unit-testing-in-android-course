package com.techyourchance.mockitofundamentals.exercise5

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus.*
import com.techyourchance.mockitofundamentals.exercise5.users.User
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.util.*

class UpdateUsernameUseCaseSyncTest {

    companion object {
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
    }

    private val updateUsernameHttpEndpointSyncMock: UpdateUsernameHttpEndpointSync = mock()
    private val usersCacheMock: UsersCache = mock()
    private val eventBusMock: EventBusPoster = mock()
    private lateinit var SUT: UpdateUsernameUseCaseSync

    @Before
    fun setUp() {
        SUT = UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSyncMock, usersCacheMock, eventBusMock)
        setupSuccess()
    }

    @Test
    fun `userId and userName to be sent to the enpoint when updating the username`() {
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        argumentCaptor<String>().apply {
            verify(updateUsernameHttpEndpointSyncMock).updateUsername(capture(), capture())
            assertEquals(2, allValues.size)
            assertEquals(USER_ID, firstValue)
            assertEquals(USER_NAME, secondValue)
        }
    }

    @Test
    fun `user to be cached to the user cache when update was successful`() {
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        argumentCaptor<User>().apply {
            verify(usersCacheMock).cacheUser(capture())
            val cachedUser = firstValue
            assertEquals(USER_NAME, cachedUser.username)
        }
    }

    @Test
    fun `user not to be cached to the user cache when GENERAL_ERROR`() {
        setupGeneralError()
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        verifyZeroInteractions(usersCacheMock)
    }

    @Test
    fun `user not to be cached to the user cache when AUTH_ERROR`() {
        setupAuthError()
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        verifyZeroInteractions(usersCacheMock)
    }

    @Test
    fun `user not to be cached to the user cache when SERVER_ERROR`() {
        setupServerError()
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        verifyZeroInteractions(usersCacheMock)
    }

    @Test
    fun `user not to be cached to the user cache when NETWORK_ERROR`() {
        setupNetworkError()
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        verifyZeroInteractions(usersCacheMock)
    }

    @Test
    fun `event is posted to the event bus when update was successful`() {
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        argumentCaptor<Any>().apply {
            verify(eventBusMock).postEvent(capture())
            val eventSent = firstValue

            assertTrue(eventSent is UserDetailsChangedEvent)
        }
    }

    @Test
    fun `event is not posted to the event bus when GENERAL_ERROR`() {
        setupGeneralError()
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        verifyZeroInteractions(eventBusMock)
    }

    @Test
    fun `event is not posted to the event bus when AUTH_ERROR`() {
        setupAuthError()
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        verifyZeroInteractions(eventBusMock)
    }

    @Test
    fun `event is not posted to the event bus when SERVER_ERROR`() {
        setupServerError()
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        verifyZeroInteractions(eventBusMock)
    }

    @Test
    fun `event is not posted to the event bus when NETWORK_ERROR`() {
        setupNetworkError()
        SUT.updateUsernameSync(USER_ID, USER_NAME)

        verifyZeroInteractions(eventBusMock)
    }

    @Test
    fun `SUCCESS is returned when update was successful`() {
        val result = SUT.updateUsernameSync(USER_ID, USER_NAME)

        assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS,result)
    }

    @Test
    fun `FAILURE is returned when AUTH_ERROR`() {
        setupAuthError()
        val result = SUT.updateUsernameSync(USER_ID, USER_NAME)

        assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE,result)
    }

    @Test
    fun `FAILURE is returned when SERVER_ERROR`() {
        setupServerError()
        val result = SUT.updateUsernameSync(USER_ID, USER_NAME)

        assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE,result)
    }

    @Test
    fun `FAILURE is returned when NETWORK_ERROR`() {
        setupNetworkError()
        val result = SUT.updateUsernameSync(USER_ID, USER_NAME)

        assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR,result)
    }

    //region endpoint responses setup
    private fun setupSuccess() {
        whenever(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(UpdateUsernameHttpEndpointSync.EndpointResult(SUCCESS, USER_ID, USER_NAME))
    }
    private fun setupGeneralError() {
        whenever(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(UpdateUsernameHttpEndpointSync.EndpointResult(GENERAL_ERROR, "", ""))
    }

    private fun setupAuthError() {
        whenever(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(UpdateUsernameHttpEndpointSync.EndpointResult(AUTH_ERROR, "", ""))
    }

    private fun setupServerError() {
        whenever(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(UpdateUsernameHttpEndpointSync.EndpointResult(SERVER_ERROR, "", ""))
    }

    private fun setupNetworkError() {
        whenever(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenThrow(NetworkErrorException())
    }
    //endregion
}