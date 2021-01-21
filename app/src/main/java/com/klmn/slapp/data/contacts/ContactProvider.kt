package com.klmn.slapp.data.contacts

import com.klmn.slapp.domain.Contact

/* this class queries the device's content resolver to get contacts.
* requires permission READ_CONTACTS to work */
interface ContactProvider {
    /* get contact name for a given phone number */
    fun getContact(phoneNumber: String): Contact?

    /* fetch all contacts matching query */
    fun fetchContacts(query: String? = null): List<Contact>
}