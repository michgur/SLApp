package com.klmn.slapp.ui.create

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.SlappList

class CreateListViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {
    fun createList(name: String) = repository.addList(SlappList(name = name))
}