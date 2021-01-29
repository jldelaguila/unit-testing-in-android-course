package com.techyourchance.testdrivendevelopment.exercise7

interface FetchReputationUseCaseSync {

    enum class Status {
        SUCCESS,
        FAILURE
    }

    fun fetchReputation(): UseCaseResult

    data class UseCaseResult(val status: Status, val reputation: Int)
}