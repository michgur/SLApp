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

    val items = listId.flatMapLatest {
        if (it.isNotEmpty()) repository.getItems(it)
        else emptyFlow()
    }

    val users = listId.flatMapLatest { id ->
        if (id.isNotEmpty()) repository.getUsers(id).map {
            it.mapNotNull(contactProvider::getContact)
        }
        else emptyFlow()
    }

    val listName = listId.flatMapLatest {
        if (it.isNotEmpty()) repository.getListName(it)
        else emptyFlow()
    }.asLiveData()

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