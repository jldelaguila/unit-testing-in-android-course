package com.techyourchance.testdoublesfundamentals.exercise4

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException
import com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.*
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync.*
import com.techyourchance.testdoublesfundamentals.exercise4.users.User
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FetchUserProfileUseCaseSyncTest {

    private lateinit var userProfileHttpEndpointSyncTd: UserProfileHttpEndpointSyncTd
    private lateinit var usersCacheTd: UsersCacheTd

    private lateinit var SUT: FetchUserProfileUseCaseSync

    companion object {
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
        const val USER_IMAGE_URL = "USER_IMAGE_URL"
    }

    @Before
    fun setUp() {
        userProfileHttpEndpointSyncTd = UserProfileHttpEndpointSyncTd()
        usersCacheTd = UsersCacheTd()
        SUT = FetchUserProfileUseCaseSync(userProfileHttpEndpointSyncTd, usersCacheTd)
    }

    @Test
    fun `send userId to the http sync object`() {
        SUT.fetchUserProfileSync(USER_ID)
        assertEquals(USER_ID, userProfileHttpEndpointSyncTd.userIdToFetch)
    }

    @Test
    fun `send user to the the user cache`() {
        assertNull(usersCacheTd.cachedUser)
        SUT.fetchUserProfileSync(USER_ID)
        assertNotNull(usersCacheTd.cachedUser)
    }

    @Test
    fun `to return FAILURE and not to cache user when authentication fails`() {
        userProfileHttpEndpointSyncTd.isAuthError = true
        val result = SUT.fetchUserProfileSync(USER_ID)
        assertEquals(UseCaseResult.FAILURE, result)
        assertNull(usersCacheTd.cachedUser)
    }

    @Test
    fun `to return FAILURE and not to cache user when a server error occurs`() {
        userProfileHttpEndpointSyncTd.isServerError = true
        val result = SUT.fetchUserProfileSync(USER_ID)
        assertEquals(UseCaseResult.FAILURE, result)
        assertNull(usersCacheTd.cachedUser)
    }

    @Test
    fun `to return FAILURE and not to cache user when a general error occurs`() {
        userProfileHttpEndpointSyncTd.isGeneralError = true
        val result = SUT.fetchUserProfileSync(USER_ID)
        assertEquals(UseCaseResult.FAILURE, result)
        assertNull(usersCacheTd.cachedUser)
    }

    @Test
    fun `to return FAILURE and not to cache user when a network error occurs`() {
        userProfileHttpEndpointSyncTd.isNetworkError = true
        val result = SUT.fetchUserProfileSync(USER_ID)
        assertEquals(UseCaseResult.NETWORK_ERROR, result)
        assertNull(usersCacheTd.cachedUser)
    }

    @Test
    fun `to return SUCCESS and to cache user when a fetching success`() {
        assertNull(usersCacheTd.cachedUser)
        val result = SUT.fetchUserProfileSync(USER_ID)
        val user = usersCacheTd.cachedUser
        assertEquals(UseCaseResult.SUCCESS, result)
        assertNotNull(user)
        assertEquals(USER_ID, user?.userId)
        assertEquals(USER_NAME, user?.fullName)
        assertEquals(USER_IMAGE_URL, user?.imageUrl)

    }

    class UserProfileHttpEndpointSyncTd : UserProfileHttpEndpointSync{
        var isAuthError: Boolean = false
        var isServerError: Boolean = false
        var isGeneralError: Boolean = false
        var isNetworkError: Boolean = false
        var userIdToFetch: String? = null

        override fun getUserProfile(id: String?): EndpointResult {
            userIdToFetch = id
            val endpointStatus = when{
                isAuthError -> EndpointResultStatus.AUTH_ERROR
                isServerError -> EndpointResultStatus.SERVER_ERROR
                isGeneralError -> EndpointResultStatus.GENERAL_ERROR
                isNetworkError -> throw NetworkErrorException()
                else -> EndpointResultStatus.SUCCESS
            }

            val userId = when{
                isAuthError || isServerError || isGeneralError -> ""
                else -> id
            }

            val userName = when{
                isAuthError || isServerError || isGeneralError -> ""
                else -> USER_NAME
            }

            val userImageUrl = when{
                isAuthError || isServerError || isGeneralError -> ""
                else -> USER_IMAGE_URL
            }

            return EndpointResult(endpointStatus, userId, userName, userImageUrl)

        }
    }

    class UsersCacheTd : UsersCache {
        var cachedUser: User? = null
        var numberOfInteractions = 0

        override fun cacheUser(user: User?) {
            this.cachedUser = user
            numberOfInteractions += 1
        }

        override fun getUser(userId: String?): User? {
            return cachedUser
        }

    }

}