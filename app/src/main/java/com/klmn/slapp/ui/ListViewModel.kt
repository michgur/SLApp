package com.klmn.slapp.ui

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.klmn.slapp.SLApp
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.SlappItem
import dagger.hilt.android.qualifiers.ApplicationContext

class ListViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listId = 290L
    private val _items = MutableLiveData<List<SlappItem>>()
    val items: LiveData<List<SlappItem>> get() = _items

    init {
        repository.getItems(listId)
                // this is awful, but currently too lazy to fix it
            .doOnSuccess { query -> (context as SLApp).mainThread.execute {
                query.asLiveData().observeForever(_items::setValue)
            }}
            .execute()
    }

    private val _user = MutableLiveData("Michael")
    val user: LiveData<String> get() = _user

    val mode = MutableLiveData(0)

    val selection: LiveData<MutableSet<SlappItem>> = MutableLiveData(mutableSetOf())

//    fun addList(name: String) =
//        repository.addList(SlappList(name = name, user = user.value ?: ""))
//            .doOnSuccess(::viewList)
//            .doOnException { Log.e("addList", it.toString()) }
//            .execute()
//
//    fun viewList(listId: Long) = repository.getItems(listId)
//        .doOnSuccess { query ->
//            (context as SLApp).mainThread.execute {
//                _listId.value = listId
//                query.asLiveData().obs { items.setValue(it) }
//            }
//        }
//        .doOnException { Log.e("viewList", it.toString()) }
//        .execute()

    fun addItem(name: String) = repository.addItem(
        listId,
        SlappItem(name, user.value ?: "")
    ).execute()

    fun deleteItem(item: SlappItem) = repository.deleteItem(listId, item).execute()
}