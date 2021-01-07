package com.klmn.slapp.data.contacts

import com.klmn.slapp.domain.Contact

interface ContactsRepository {
    suspend fun getContact(phoneNumber: String): Contact?
    suspend fun fetchContacts(query: String? = null): List<Contact>
}