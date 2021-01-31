package com.techyourchance.unittesting.questions

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.techyourchance.unittesting.common.time.TimeProvider
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint
import com.techyourchance.unittesting.networking.questions.QuestionSchema
import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase.Listener
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class FetchQuestionDetailsUseCaseTest {

    companion object {
        const val TEST_QUESTION_ID = "TEST_QUESTION_ID"
        const val TEST_TITLE = "TEST_TITLE"
        const val TEST_BODY = "TEST_BODY"
        const val TEST_TIMESTAMP = 100000L
        const val TEST_TIMEOUT_TIMESTAMP_WINDOW = 60001L
    }

    private val fetchQuestionDetailsEndpointMock: FetchQuestionDetailsEndpoint = mock()
    private val timeProviderMock: TimeProvider = mock()
    private val listenerMock1: Listener = mock()
    private val listenerMock2: Listener = mock()
    private lateinit var SUT: FetchQuestionDetailsUseCase

    @Before
    fun setUp() {
        SUT = FetchQuestionDetailsUseCase(fetchQuestionDetailsEndpointMock, timeProviderMock)
        success()
    }

    @Test
    fun `WHEN fetching question detail, THEN send questionId to endpoint`() {
        // given
        // when
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        // then
        argumentCaptor<String> {
            verify(fetchQuestionDetailsEndpointMock).fetchQuestionDetails(capture(), any())
            assertEquals(TEST_QUESTION_ID, firstValue)
        }

    }

    @Test
    fun `WHEN fetch is successful, THEN notify all listeners`() {
        // given
        SUT.registerListener(listenerMock1)
        SUT.registerListener(listenerMock2)
        // when
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        // then
        verify(listenerMock1).onQuestionDetailsFetched(getQuestionDetails())
        verify(listenerMock2).onQuestionDetailsFetched(getQuestionDetails())
    }

    @Test
    fun `WHEN fetch is successful, THEN notify only subscribed listeners`() {
        // given
        SUT.registerListener(listenerMock1)
        SUT.registerListener(listenerMock2)
        SUT.unregisterListener(listenerMock2)
        // when
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        // then
        verify(listenerMock1).onQuestionDetailsFetched(getQuestionDetails())
        verifyZeroInteractions(listenerMock2)
    }

    @Test
    fun `WHEN fetch fails, THEN notify all listners of the error`() {
        // given
        error()
        SUT.registerListener(listenerMock1)
        SUT.registerListener(listenerMock2)
        // when
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        // then
        verify(listenerMock1).onQuestionDetailsFetchFailed()
        verify(listenerMock2).onQuestionDetailsFetchFailed()
    }

    @Test
    fun `WHEN fetch fails, THEN notify only subscribed listners of the error`() {
        // given
        error()
        SUT.registerListener(listenerMock1)
        SUT.registerListener(listenerMock2)
        SUT.unregisterListener(listenerMock2)
        // when
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        // then
        verify(listenerMock1).onQuestionDetailsFetchFailed()
        verifyZeroInteractions(listenerMock2)
    }

    @Test
    fun `WHEN question details was cached, THEN endpoint not invoked`() {
        // given
        // when
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        // then
        argumentCaptor<String> {
            verify(fetchQuestionDetailsEndpointMock).fetchQuestionDetails(capture(), any())
            assertEquals(TEST_QUESTION_ID, firstValue)
            verifyNoMoreInteractions(fetchQuestionDetailsEndpointMock)
        }
    }

    @Test
    fun `WHEN question details cached expired, THEN endpoint invoked`() {
        // given
        // when
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        expiredCacheTimestamp()
        SUT.fetchQuestionDetailsAndNotify(TEST_QUESTION_ID)
        // then
        argumentCaptor<String> {
            verify(fetchQuestionDetailsEndpointMock, times(2)).fetchQuestionDetails(capture(), any())
            assertEquals(TEST_QUESTION_ID, firstValue)
        }
    }

    //region helper methods
    private fun success() {
        doAnswer {
            val listener = it.arguments[1] as FetchQuestionDetailsEndpoint.Listener
            listener.onQuestionDetailsFetched(getQuestionSchema())
        }.whenever(fetchQuestionDetailsEndpointMock).fetchQuestionDetails(anyString(), any())

        whenever(timeProviderMock.currentTimestamp).thenReturn(TEST_TIMESTAMP)
    }

    private fun error() {
        doAnswer {
            val listener = it.arguments[1] as FetchQuestionDetailsEndpoint.Listener
            listener.onQuestionDetailsFetchFailed()
        }.whenever(fetchQuestionDetailsEndpointMock).fetchQuestionDetails(anyString(), any())
    }

    fun expiredCacheTimestamp() {
        whenever(timeProviderMock.currentTimestamp).thenReturn(TEST_TIMESTAMP + TEST_TIMEOUT_TIMESTAMP_WINDOW)
    }

    private fun getQuestionSchema() = QuestionSchema(
            TEST_TITLE,
            TEST_QUESTION_ID,
            TEST_BODY
    )

    private fun getQuestionDetails() = QuestionDetails(
            TEST_QUESTION_ID,
            TEST_TITLE,
            TEST_BODY
    )
    //endregion
}