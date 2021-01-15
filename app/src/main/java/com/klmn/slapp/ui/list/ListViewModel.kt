package com.klmn.slapp.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.contacts.ContactsRepository
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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

    // we need a StateFlow here since this data is used both in the ItemsTab and the UserTab
    lateinit var users: StateFlow<List<Contact>>
    init {
        viewModelScope.launch {
            users = listId.flatMapLatest { id ->
                if (id.isNotEmpty()) repository.getUsers(id).map {
                    it.mapNotNull(contactProvider::getContact)
                } else emptyFlow()
            }.stateIn(viewModelScope)
        }
    }

    val listName = listId.flatMapLatest {
        if (it.isNotEmpty()) repository.getListName(it)
        else emptyFlow()
    }.asLiveData()

    val selectionModeEnabled = MutableLiveData(false)
    val selectionModeTitle = MutableLiveData<String>()

    val selection = mutableSetOf<SlappItem>()

    val enterItemEnabled = MutableLiveData(false)

    val bottomSheetState = MutableLiveData(5)

    fun addItem(name: String) = viewModelScope.launch {
        repository.addItem(listId.value, SlappItem(name, userPreferences.phoneNumber.value ?: ""))
    }

    fun deleteItem(item: SlappItem) = viewModelScope.launch {
        repository.deleteItem(listId.value, item)
    }
}