package com.klmn.slapp.ui.list.info.addUsers

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.contacts.ContactsRepository
import com.klmn.slapp.domain.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class AddUsersViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    private val contactsRepository: ContactsRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val selection = mutableSetOf<Contact>()

    val query = MutableLiveData("")

    val contacts = query.asFlow().mapLatest {
        contactsRepository.fetchContacts(it)
    }

    fun addUsers(listId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.addUsers(listId, selection)
        }
    }
}