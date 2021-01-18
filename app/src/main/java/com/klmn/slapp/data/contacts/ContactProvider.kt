package com.klmn.slapp.data.contacts

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone.*
import com.klmn.slapp.R
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.Contact

class ContactProvider(
    private val context: Context,
    private val userPreferences: UserPreferences
) {
    private val projection = arrayOf(
        NORMALIZED_NUMBER,
        DISPLAY_NAME_PRIMARY
    )

    fun getContact(phoneNumber: String): Contact? {
        if (phoneNumber == userPreferences.phoneNumber.value)
            return Contact(phoneNumber, context.getString(R.string.contact_you))
        else if (userPreferences.hasReadContactsPermission.value != true) return null

        val uri = Uri.withAppendedPath(CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        return cursor?.run {
            moveToFirst()
            Contact(
                phoneNumber,
                getString(getColumnIndex(DISPLAY_NAME_PRIMARY))
            )
        }.also { cursor?.close() }
    }

    fun fetchContacts(query: String? = null): List<Contact> {
        if (userPreferences.hasReadContactsPermission.value != true) return listOf()
        val contacts = mutableListOf<Contact>()
        context.contentResolver.query(
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