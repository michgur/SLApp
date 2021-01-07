package com.klmn.slapp.data.contacts

import com.klmn.slapp.domain.Contact

interface ContactsRepository {
    fun getContact(phoneNumber: String): Contact?
    fun fetchContacts(query: String? = null): List<Contact>
}