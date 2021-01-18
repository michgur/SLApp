package com.klmn.slapp.data.contacts

/* an implementation of ContactsRepository that uses a ContactProvider */
class ContactsRepositoryImpl(private val contactProvider: ContactProvider) : ContactsRepository {
    override fun getContact(phoneNumber: String) = contactProvider.getContact(phoneNumber)
    override fun fetchContacts(query: String?) = contactProvider.fetchContacts(query)
}