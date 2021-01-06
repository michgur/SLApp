package com.klmn.slapp.ui.list

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.domain.SlappItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ListViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SlappRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val listId = MutableStateFlow(0L)

    val items = listId.flatMapLatest {
        if (it != 0L) repository.getItems(it)
        else emptyFlow()
    }

    val listName = listId.flatMapLatest {
        if (it != 0L) repository.getListName(it)
        else emptyFlow()
    }

    val selectionModeEnabled = MutableLiveData(false)

    // i don't think there is a point in having this as a liveData
    val selection: LiveData<MutableSet<SlappItem>> = MutableLiveData(mutableSetOf())

    fun addItem(name: String) = viewModelScope.launch {
        repository.addItem(listId.value, SlappItem(name, "Michael"))
    }

    fun deleteItem(item: SlappItem) = viewModelScope.launch {
        repository.deleteItem(listId.value, item)
    }
}