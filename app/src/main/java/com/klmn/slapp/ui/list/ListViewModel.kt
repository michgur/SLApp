package com.klmn.slapp.ui.list

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.klmn.slapp.SLApp
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.SlappItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

class ListViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _items = MutableStateFlow<List<SlappItem>>(listOf())
    val items: LiveData<List<SlappItem>> get() = _items.asLiveData()

    private val _listName = MutableLiveData<String>()
    val listName: LiveData<String> get() = _listName

    var listId = 0L
        set(value) {
            field = value
            repository.getListName(listId)
                .doOnSuccess {
                    (context as SLApp).mainThread.execute { _listName.setValue(it) }
                }
                .execute()
            repository.getItems(listId)
                .doOnSuccess { query ->
                    viewModelScope.launch {
                        _items.emitAll(query)
                    }
                }
                .execute()
        }

    val mode = MutableLiveData(0)

    val selection: LiveData<MutableSet<SlappItem>> = MutableLiveData(mutableSetOf())

    fun addItem(name: String) = repository.addItem(
        listId,
        SlappItem(name, "Michael")
    ).execute()

    fun deleteItem(item: SlappItem) = repository.deleteItem(listId, item).execute()
}