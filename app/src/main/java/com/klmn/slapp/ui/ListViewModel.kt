package com.klmn.slapp.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

class ListViewModel @ViewModelInject constructor(
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _listId = MutableLiveData<Long>()
    val listId: LiveData<Long> get() = _listId

    lateinit var items: LiveData<List<SlappItem>> private set

    private val _user = MutableLiveData("Michael")
    val user: LiveData<String> get() = _user

    fun addList(name: String) = repository.addList(SlappList(
        0,
        name,
        user.value ?: "",
        System.currentTimeMillis() / 1000L,
        mutableListOf(),
        listOf(user.value ?: "")
    )).doOnSuccess(::viewList)
        .doOnException { TODO() }
        .execute()

    fun viewList(listId: Long) {
        _listId.value = listId
        repository.getItems(listId)
            .doOnSuccess { items = it }
            .doOnException { TODO("implement error messages") }
            .execute()
    }

    fun addItem(name: String) = repository.addItem(
        listId.value ?: 0,
        SlappItem(
            name,
            user.value ?: "",
            System.currentTimeMillis() / 1000L
        )
    ).execute()
}