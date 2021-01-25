package com.klmn.slapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.asLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/* caches in a DataStore user details & preferences */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.createDataStore(DATASTORE_NAME)

    val phoneNumber = dataStore.data.map {
        it[KEY_PHONE_NUMBER] ?: ""
    }.asLiveData()

    val registrationToken = dataStore.data.map {
        it[KEY_REGISTRATION_TOKEN]
    }.asLiveData()

    val hasReadContactsPermission = dataStore.data.map {
        it[KEY_CONTACT_PERMISSION] ?: false
    }.asLiveData()

    val favorites = dataStore.data.map {
        it[KEY_FAVORITES]
    }.filterNotNull().asLiveData()

    suspend fun savePhoneNumber(number: String) = dataStore.edit { it[KEY_PHONE_NUMBER] = number }
    suspend fun saveRegistrationToken(token: String) =
        dataStore.edit { it[KEY_REGISTRATION_TOKEN] = token }
    suspend fun saveHasReadContactsPermission(value: Boolean) =
        dataStore.edit { it[KEY_CONTACT_PERMISSION] = value }

    suspend fun addToFavorites(listId: String) =
        dataStore.edit {
            if(it[KEY_FAVORITES].isNullOrEmpty()) it[KEY_FAVORITES] = setOf(listId)
            else it[KEY_FAVORITES] = it[KEY_FAVORITES]!! + listId
        }
    suspend fun removeFromFavorites(listId: String) =
        dataStore.edit {
            if(it[KEY_FAVORITES] != null)
                it[KEY_FAVORITES] = it[KEY_FAVORITES]!! - listId
        }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit {
                if (it[KEY_FAVORITES] == null) it[KEY_FAVORITES] = setOf()
            }
        }
    }

    companion object {
        private const val DATASTORE_NAME = "slapp_ds"

        private val KEY_PHONE_NUMBER = stringPreferencesKey("key_phone_number")
        private val KEY_REGISTRATION_TOKEN = stringPreferencesKey("key_registration_token")
        private val KEY_CONTACT_PERMISSION = booleanPreferencesKey("key_contact_permission")
        private val KEY_FAVORITES = stringSetPreferencesKey("key_favorites")
    }
}