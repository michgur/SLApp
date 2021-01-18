package com.klmn.slapp.data.contacts

import com.klmn.slapp.domain.Contact

/* retrieves contacts from a source.
* functions are not suspend but should'nt be called on the UI thread */
interface ContactsRepository {
    fun getContact(phoneNumber: String): Contact?
    fun fetchContacts(query: String? = null): List<Contact>
}