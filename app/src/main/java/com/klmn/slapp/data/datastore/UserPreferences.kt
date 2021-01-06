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

    val uid = dataStore.data.map { it[KEY_UID] }.asLiveData()

    suspend fun saveUID(uid: String) = dataStore.edit { it[KEY_UID] = uid }

    companion object {
        private val KEY_UID = preferencesKey<String>("key_uid")
    }
}