package com.techyourchance.testdrivendevelopment.exercise6

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.*
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException
import com.techyourchance.testdrivendevelopment.exercise6.users.User
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache

internal class FetchUserUseCase(
        private val fetchUserHttpEndpointSync: FetchUserHttpEndpointSync,
        private val usersCache: UsersCache
) : FetchUserUseCaseSync {

    override fun fetchUserSync(userId: String?): UseCaseResult {
        return usersCache.getUser(userId)?.let {
            UseCaseResult(Status.SUCCESS, it)
        } ?: run {
            try {
                val result = fetchUserHttpEndpointSync.fetchUserSync(userId)
                when (result.status) {
                    FetchUserHttpEndpointSync.EndpointStatus.SUCCESS -> {
                        val fetchedUser = User(result.userId, result.username)
                        usersCache.cacheUser(fetchedUser)
                        UseCaseResult(Status.SUCCESS, fetchedUser)
                    }
                    else -> UseCaseResult(Status.FAILURE, null)
                }
            } catch (networkException: NetworkErrorException) {
                UseCaseResult(Status.NETWORK_ERROR, null)
            }
        }
    }
}