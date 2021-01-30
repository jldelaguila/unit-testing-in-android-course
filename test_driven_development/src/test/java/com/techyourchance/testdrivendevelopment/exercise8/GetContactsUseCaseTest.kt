package com.techyourchance.testdrivendevelopment.exercise8

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.techyourchance.testdrivendevelopment.exercise8.GetContactsUseCase.FetchContactsListener
import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class GetContactsUseCaseTest {

    companion object {
        const val TEST_FILTER_TERM = "TEST_FILTER_TERM"
        const val TEST_ID = "id"
        const val TEST_FULL_NAME = "fullName"
        const val TEST_PHONE_NUMBER = "fullPhoneNumber"
        const val TEST_IMAGE_URL = "imageUrl"
        const val TEST_AGE = 12.0
    }

    private val listener1: FetchContactsListener = mock()
    private val listener2: FetchContactsListener = mock()
    private val getContactsHttpEndpointMock: GetContactsHttpEndpoint = mock()
    private lateinit var SUT: GetContactsUseCase

    @Before
    fun setUp() {
        SUT = GetContactsUseCase(getContactsHttpEndpointMock)
    }

    @Test
    fun `WHEN fetching contacts, THEN filter term is passed to endpoint`() {
        // given
        // when
        SUT.fetchContactsAndNotify(TEST_FILTER_TERM)
        // then
        argumentCaptor<String> {
            verify(getContactsHttpEndpointMock).getContacts(capture(), any())
            assertEquals(TEST_FILTER_TERM, firstValue)
        }
    }

    @Test
    fun `WHEN fetch is successful, THEN all listeners are notified`() {
        // given
        success()
        // when
        SUT.subscribeListener(listener1)
        SUT.subscribeListener(listener2)
        SUT.fetchContactsAndNotify(TEST_FILTER_TERM)
        // then
        argumentCaptor<List<Contact>> {
            verify(listener1).onContactsFetchSuccessful(capture())
            verify(listener2).onContactsFetchSuccessful(capture())
            allValues.forEach {
                assertEquals(getContacts(), it)
            }
        }
    }

    @Test
    fun `WHEN fetch is successful, THEN only subscribed listeners are notified`() {
        // given
        success()
        // when
        SUT.subscribeListener(listener1)
        SUT.subscribeListener(listener2)
        SUT.unsubscribeListener(listener2)
        SUT.fetchContactsAndNotify(TEST_FILTER_TERM)
        // then
        argumentCaptor<List<Contact>> {
            verify(listener1).onContactsFetchSuccessful(capture())
            allValues.forEach {
                assertEquals(getContacts(), it)
            }
            verifyZeroInteractions(listener2)
        }
    }

    @Test
    fun `WHEN GENERAL_ERROR, THEN listeners are notified of the failure`() {
        // given
        generalError()
        // when
        SUT.subscribeListener(listener1)
        SUT.subscribeListener(listener2)
        SUT.fetchContactsAndNotify(TEST_FILTER_TERM)
        // then
        verify(listener1).onContactsFetchFailed()
        verify(listener2).onContactsFetchFailed()
    }

    @Test
    fun `WHEN GENERAL_ERROR, THEN only subscribed listeners are notified of the failure`() {
        // given
        generalError()
        // when
        SUT.subscribeListener(listener1)
        SUT.subscribeListener(listener2)
        SUT.unsubscribeListener(listener2)
        SUT.fetchContactsAndNotify(TEST_FILTER_TERM)
        // then
        verify(listener1).onContactsFetchFailed()
        verifyZeroInteractions(listener2)
    }

    @Test
    fun `WHEN NETWORK_ERROR, THEN listeners are notified of the failure`() {
        // given
        networkError()
        // when
        SUT.subscribeListener(listener1)
        SUT.subscribeListener(listener2)
        SUT.fetchContactsAndNotify(TEST_FILTER_TERM)
        // then
        verify(listener1).onContactsFetchFailed()
        verify(listener2).onContactsFetchFailed()
    }

    @Test
    fun `WHEN NETWORK_ERROR, THEN only subscribed listeners are notified of the failure`() {
        // given
        networkError()
        // when
        SUT.subscribeListener(listener1)
        SUT.subscribeListener(listener2)
        SUT.unsubscribeListener(listener2)
        SUT.fetchContactsAndNotify(TEST_FILTER_TERM)
        // then
        verify(listener1).onContactsFetchFailed()
        verifyZeroInteractions(listener2)
    }

    //region helper methods
    private fun success() {
        doAnswer {
            val listener = it.arguments[1] as Callback
            listener.onGetContactsSucceeded(getContactSchemas())
        }.whenever(getContactsHttpEndpointMock).getContacts(anyString(), any())
    }

    private fun generalError() {
        doAnswer {
            val listener = it.arguments[1] as Callback
            listener.onGetContactsFailed(FailReason.GENERAL_ERROR)
        }.whenever(getContactsHttpEndpointMock).getContacts(anyString(), any())
    }

    private fun networkError() {
        doAnswer {
            val listener = it.arguments[1] as Callback
            listener.onGetContactsFailed(FailReason.NETWORK_ERROR)
        }.whenever(getContactsHttpEndpointMock).getContacts(anyString(), any())
    }

    private fun getContactSchemas(): List<ContactSchema> {
        return listOf(
                ContactSchema(
                        TEST_ID,
                        TEST_FULL_NAME,
                        TEST_PHONE_NUMBER,
                        TEST_IMAGE_URL,
                        TEST_AGE
                )
        )
    }

    private fun getContacts(): List<Contact> {
        return listOf(
                Contact(
                        TEST_ID,
                        TEST_FULL_NAME,
                        TEST_IMAGE_URL
                )
        )
    }
    //endregion
}