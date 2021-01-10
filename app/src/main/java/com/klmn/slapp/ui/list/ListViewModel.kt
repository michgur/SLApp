package com.klmn.slapp.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.contacts.ContactsRepository
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.SlappItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ListViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    private val userPreferences: UserPreferences,
    private val contactProvider: ContactsRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listId = MutableStateFlow("")

    private val list = listId.flatMapLatest {
        if (it.isNotEmpty()) repository.getList(it)
        else emptyFlow()
    }

    // not great, every time anything in the list changes all of these are being called
    // maybe better than having 3 snapshot listeners tho
    val items = list.map {
        println(it)
        it.items
    }
    val users = list.map { it.users.mapNotNull(contactProvider::getContact) }
    val listName = list.map { it.name }.asLiveData()

    val selectionModeEnabled = MutableLiveData(false)
    val selectionModeTitle = MutableLiveData<String>()

    val selection = mutableSetOf<SlappItem>()

    fun addItem(name: String) = viewModelScope.launch {
        repository.addItem(listId.value, SlappItem(name, userPreferences.phoneNumber.value ?: ""))
    }

    fun deleteItem(item: SlappItem) = viewModelScope.launch {
        repository.deleteItem(listId.value, item)
    }
}