package com.klmn.slapp.ui.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.klmn.slapp.data.SlappRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class HomeViewModel @ViewModelInject constructor(
    repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listsFlow = repository.getLists()

    var position = 0
}