package com.klmn.slapp.ui.create

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.launch

class CreateListViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {
    fun createList(name: String, callback: (Long) -> Unit) = viewModelScope.launch {
        callback(repository.addList(SlappList(name = name)))
    }
}