package com.klmn.slapp.ui.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappList
import com.klmn.slapp.domain.TokenRequest
import com.klmn.slapp.messaging.fcm.NotificationAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    private val userPreferences: UserPreferences,
    private val notificationAPI: NotificationAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listsFlow = userPreferences.phoneNumber.asFlow().flatMapLatest {
        repository.getLists(it)
    }

    var position = 0

    val hidePermissionRequest = userPreferences.hasReadContactsPermission

    fun createList(name: String, callback: (String) -> Unit) = viewModelScope.launch {
        val list = SlappList(
            name = name,
            user = Contact(userPreferences.phoneNumber.value
                ?: throw IllegalStateException("not signed in"))
        )
        val id = repository.addList(list)
        callback(id)
        TokenRequest(
            TokenRequest.CREATE,
            id,
            "",
            listOf(userPreferences.registrationToken.value
                ?: throw IllegalStateException("no registration token"))
        ).let { notificationAPI.postTokenRequest(it) }.let { response ->
            if (response.isSuccessful) response.body() // ok i need to learn retrofit
            // eventually- repository.setListNotificationKey(id, KEY)
        }
    }
}