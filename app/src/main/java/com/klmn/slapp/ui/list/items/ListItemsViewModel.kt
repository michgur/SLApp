package com.klmn.slapp.ui.list.items

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.BuyNotification
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
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

    // todo: -move this to MessagingService
    //      -figure out why the title & message aren't showing
    //      -remove retrofit & list.isNew
    //          -consider changing the 'users' collection to also have groups (minimize doc reads on every message)
    //      -change the message payload to something useful
    //      security rules for functions & firestore
    //      move on to the final thing- testing- create a scenario with some fake users

    fun finishShopping(success: Boolean) {
        if (success) {
            BuyNotification(
                listId.value,
                userPreferences.phoneNumber.value!!,
                System.currentTimeMillis() * 1000L,
                cartItems.value
            ).let {
                Firebase.functions("europe-west1").getHttpsCallable("sendMessage")
                    .call(Gson().toJson(it))
                    .addOnCompleteListener { task ->
                        println("success: ${task.isSuccessful}")
                        if (!task.isSuccessful) task.exception?.printStackTrace()
                    }
            }
        }
        cartItems.value = listOf()
    }
}