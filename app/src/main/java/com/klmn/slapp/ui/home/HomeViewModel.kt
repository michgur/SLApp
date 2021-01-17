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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    private val userPreferences: UserPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listsFlow = userPreferences.phoneNumber.asFlow().flatMapLatest {
        repository.getLists(it)
    }

    var position = 0

    fun createList(name: String, callback: (String) -> Unit) = viewModelScope.launch {
        callback(repository.addList(
            SlappList(
                name = name,
                user = Contact(userPreferences.phoneNumber.value ?: "", "You")
            )
        ))
    }
}