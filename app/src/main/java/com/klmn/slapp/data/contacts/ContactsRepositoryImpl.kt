package com.klmn.slapp.data.contacts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepositoryImpl(private val contactProvider: ContactProvider) : ContactsRepository {
    override fun getContact(phoneNumber: String) = //withContext(Dispatchers.IO) {
        contactProvider.getContact(phoneNumber)

    override fun fetchContacts(query: String?) = //withContext(Dispatchers.IO) {
        contactProvider.fetchContacts(query)
}