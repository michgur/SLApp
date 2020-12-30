package com.klmn.slapp.ui

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.klmn.slapp.SLApp
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext

class ListViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    @ActivityContext private val context: Context,
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _listId = MutableLiveData<Long>()
    val listId: LiveData<Long> get() = _listId

    // figure out how people bind room liveData to viewModel liveData
    private val _items = MutableLiveData<List<SlappItem>>()
    val items: LiveData<List<SlappItem>> get() = _items

    private val _user = MutableLiveData("Michael")
    val user: LiveData<String> get() = _user

    fun addList(name: String) =
        repository.addList(SlappList(name = name, user = user.value ?: ""))
            .doOnSuccess(::viewList)
            .doOnException { Log.e("addList", it.toString()) }
            .execute()

    fun viewList(listId: Long) = repository.getItems(listId)
        .doOnSuccess { items ->
            (appContext as SLApp).mainThread.execute {
                _listId.value = listId
                items.observe(context as LifecycleOwner, _items::setValue)
            }
        }
        .doOnException { Log.e("viewList", it.toString()) }
        .execute()

    fun addItem(name: String) = repository.addItem(
        listId.value ?: 0,
        SlappItem(name, user.value ?: "")
    ).execute()

    fun deleteItem(item: SlappItem) = repository.deleteItem(listId.value ?: 0, item).execute()
}