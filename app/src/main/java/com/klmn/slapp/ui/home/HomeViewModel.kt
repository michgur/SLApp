package com.klmn.slapp.ui.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@ExperimentalCoroutinesApi
class HomeViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listsFlow = repository.getLists()
}