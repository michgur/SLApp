package com.klmn.slapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.asLiveData
import com.klmn.slapp.common.DATASTORE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.createDataStore(DATASTORE_NAME)

    val phoneNumber = dataStore.data.map {
        it[KEY_PHONE_NUMBER] ?: ""
    }.asLiveData()

    val hasReadContactsPermission = dataStore.data.map {
        it[KEY_CONTACT_PERMISSION] ?: false
    }.asLiveData()

    suspend fun savePhoneNumber(number: String) = dataStore.edit { it[KEY_PHONE_NUMBER] = number }
    suspend fun saveHasReadContactsPermission(value: Boolean) =
        dataStore.edit { it[KEY_CONTACT_PERMISSION] = value }

    companion object {
        private val KEY_PHONE_NUMBER = preferencesKey<String>("key_phone_number")
        private val KEY_CONTACT_PERMISSION = preferencesKey<Boolean>("key_contact_permission")
    }
}