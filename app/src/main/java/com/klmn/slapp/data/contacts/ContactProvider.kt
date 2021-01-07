package com.klmn.slapp.data.contacts

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone.*
import com.klmn.slapp.domain.Contact

class ContactProvider(private val contentResolver: ContentResolver) {
    private val projection = arrayOf(
        NORMALIZED_NUMBER,
        DISPLAY_NAME_PRIMARY
    )

    fun getContact(phoneNumber: String): Contact? {
        val uri = Uri.withAppendedPath(CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return cursor?.run {
            moveToFirst()
            Contact(
                phoneNumber,
                getString(getColumnIndex(DISPLAY_NAME_PRIMARY))
            )
        }.also { cursor?.close() }
    }

    fun fetchContacts(query: String? = null): List<Contact> {
        val contacts = mutableListOf<Contact>()
        contentResolver.query(
            CONTENT_URI,
            projection,
            query?.run { "$DISPLAY_NAME_PRIMARY LIKE ?" },
            query?.run { arrayOf("%$this%") },
            DISPLAY_NAME_PRIMARY
        )?.apply {
            moveToFirst()
            while (!isAfterLast) {
                contacts += Contact(
                    getString(getColumnIndex(NORMALIZED_NUMBER)) ?: "",
                    getString(getColumnIndex(DISPLAY_NAME_PRIMARY))
                )
                moveToNext()
            }
            close()
        }

        return contacts.apply {
            removeAll { it.phoneNumber.isBlank() }
        }
    }
}