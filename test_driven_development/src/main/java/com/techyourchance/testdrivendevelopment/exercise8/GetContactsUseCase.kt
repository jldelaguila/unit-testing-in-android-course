package com.techyourchance.testdrivendevelopment.exercise8

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason

class GetContactsUseCase(
        private val contactsHttpEndpoint: GetContactsHttpEndpoint
) {

    private val listeners = mutableListOf<FetchContactsListener>()

    interface FetchContactsListener {
        fun onContactsFetchSuccessful(contacts: List<Contact>)
        fun onContactsFetchFailed()
    }

    fun fetchContactsAndNotify(filterTerm: String) {
        contactsHttpEndpoint.getContacts(filterTerm, object : Callback {
            override fun onGetContactsSucceeded(contactSchemas: MutableList<ContactSchema>) {
                listeners.forEach {
                    it.onContactsFetchSuccessful(contactsFromContactSchemas(contactSchemas))
                }
            }

            override fun onGetContactsFailed(failReason: FailReason) {
                if (failReason == FailReason.GENERAL_ERROR || failReason == FailReason.NETWORK_ERROR) {
                    listeners.forEach {
                        it.onContactsFetchFailed()
                    }
                }
            }
        })
    }

    fun subscribeListener(listener: FetchContactsListener) {
        listeners.add(listener)
    }

    fun unsubscribeListener(listener: FetchContactsListener) {
        listeners.remove(listener)
    }

    fun contactsFromContactSchemas(contactSchemas: List<ContactSchema>) = contactSchemas.map {
        Contact(it.id, it.fullName, it.imageUrl)
    }
}