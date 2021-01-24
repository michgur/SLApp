package com.klmn.slapp.ui.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappList
import com.klmn.slapp.domain.TokenRequest
import com.klmn.slapp.messaging.fcm.NotificationAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
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
    }.combine(userPreferences.favorites.asFlow()) { lists, favorites ->
        lists.sortedWith(compareBy<SlappList> {
            it.id !in favorites
        }.thenByDescending {
            if (it.items.isEmpty()) it.timestamp
            else it.items[it.items.size - 1].timestamp
        })
    }

    val favorites = userPreferences.favorites

    var position = 0

    val hidePermissionRequest = userPreferences.hasReadContactsPermission

    fun flipFavorite(listId: String) = viewModelScope.launch {
        if (favorites.value?.contains(listId) != true) userPreferences.addToFavorites(listId)
        else userPreferences.removeFromFavorites(listId)
    }

    // server functions:
    //      update token
    //      create list
    //      add users to list
    // needs to keep track of:
    //      uid-token relations
    //      users in list

    fun createList(name: String, callback: (String) -> Unit) = viewModelScope.launch {
        val list = SlappList(
            name = name,
            user = Contact(userPreferences.phoneNumber.value
                ?: throw IllegalStateException("not signed in"))
        )
        val id = repository.addList(list)
        callback(id)
//        TokenRequest(
//            TokenRequest.CREATE,
//            id,
//            "",
//            listOf(userPreferences.registrationToken.value
//                ?: throw IllegalStateException("no registration token"))
//        ).let { notificationAPI.postTokenRequest(it) }.let { response ->
//            if (response.isSuccessful) response.body() // ok i need to learn retrofit
//            // eventually- repository.setListNotificationKey(id, KEY)
//        }
    }
}