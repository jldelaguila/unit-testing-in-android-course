package com.techyourchance.testdrivendevelopment.exercise7

import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.Status
import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.UseCaseResult
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus

class FetchReputationUseCase(
        private val reputationHttpEndpointSync: GetReputationHttpEndpointSync
) : FetchReputationUseCaseSync {

    override fun fetchReputation(): UseCaseResult {
        val result = reputationHttpEndpointSync.getReputationSync()
        return when {
            result.status == EndpointStatus.SUCCESS -> UseCaseResult(Status.SUCCESS, result.reputation)
            else -> UseCaseResult(Status.FAILURE, result.reputation)
        }
    }
}
