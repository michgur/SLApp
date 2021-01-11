package com.klmn.slapp.data.contacts

class ContactsRepositoryImpl(private val contactProvider: ContactProvider) : ContactsRepository {
    override fun getContact(phoneNumber: String) = //withContext(Dispatchers.IO) {
        contactProvider.getContact(phoneNumber)

    override fun fetchContacts(query: String?) = //withContext(Dispatchers.IO) {
        contactProvider.fetchContacts(query)
}