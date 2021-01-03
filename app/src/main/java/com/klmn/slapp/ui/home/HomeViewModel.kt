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
    init {
        repository.getLists()
                .doOnSuccess { lists -> lists.forEach {
                    _listsFlow.value = ListState.GotList(it)
                }}
                .doOnException {}
                .execute()
    }

    private val _listsFlow = MutableStateFlow<ListState>(ListState.NoList)
    val listsFlow: StateFlow<ListState> get() = _listsFlow

    sealed class ListState {
        class GotList(val list: SlappList) : ListState()
        object NoList : ListState()
    }
}