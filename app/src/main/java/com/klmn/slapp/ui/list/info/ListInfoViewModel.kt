package com.klmn.slapp.ui.list.info

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ListInfoViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listId = MutableStateFlow("")

    private val _users = MutableStateFlow(listOf<Contact>())
    val users: Flow<List<Contact>> get() = _users
    init {
        viewModelScope.launch {
            _users.emitAll(
                listId.flatMapLatest { id ->
                    if (id.isNotEmpty()) repository.getUsers(id)
                    else emptyFlow()
                }
            )
        }
    }

    val listName = listId.flatMapLatest {
        if (it.isNotEmpty()) repository.getListName(it)
        else emptyFlow()
    }.asLiveData()

    fun setListName(name: String) = viewModelScope.launch {
        repository.setListName(listId.value, name)
    }
}