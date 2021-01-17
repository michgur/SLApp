package com.klmn.slapp.ui.list.items

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ListItemsViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    private val userPreferences: UserPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listId = MutableStateFlow("")

    private val items = listId.flatMapLatest {
        if (it.isNotEmpty()) repository.getItems(it)
        else emptyFlow()
    }

    val cartItems = MutableStateFlow(listOf<SlappItem>())

    val listItems = items.combine(cartItems) { items, cart ->
        items.filterNot { it in cart }
    }

    lateinit var users: LiveData<List<Contact>>
    init {
        viewModelScope.launch {
            users = listId.flatMapLatest {
                if (it.isNotEmpty()) repository.getUsers(it)
                else emptyFlow()
            }.asLiveData()
        }
    }

    val listName = listId.flatMapLatest {
        if (it.isNotEmpty()) repository.getListName(it)
        else emptyFlow()
    }.asLiveData()

    val selectionModeEnabled = MutableLiveData(false)
    val selectionModeTitle = MutableLiveData<String>()

    val selection = mutableSetOf<SlappItem>()

    val shoppingModeEnabled = MutableLiveData(false)

    fun addItem(name: String) = viewModelScope.launch {
        repository.addItem(
            listId.value,
            SlappItem(name, Contact(userPreferences.phoneNumber.value ?: "", "You"))
        )
    }

    fun deleteItem(item: SlappItem) = viewModelScope.launch {
        repository.deleteItem(listId.value, item)
    }

    fun finishShopping(success: Boolean) {
        if (success) cartItems.value.forEach(::deleteItem)
        cartItems.value = listOf()
    }
}