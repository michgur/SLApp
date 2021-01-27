package com.klmn.slapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.flow.mapNotNull

class FakeRepository : SlappRepository {
    override suspend fun addList(list: SlappList) = list.hashCode().toString(16).also {
        lists.value = lists.value?.toMutableMap()?.apply { set(it, list) }
    }

    override suspend fun getLists(uid: String) = lists.map { it.values.toList() }.asFlow()

    override suspend fun getListName(listId: String) =
        lists.asFlow().mapNotNull { it[listId]?.name }

    override suspend fun setListName(listId: String, name: String) {
        lists.value = lists.value?.toMutableMap()?.apply {
            get(listId)?.let { set(listId, it.copy(name = name)) }
        }
    }

    override suspend fun addItem(listId: String, item: SlappItem) {
        lists.value = lists.value?.toMutableMap()?.apply {
            get(listId)?.let { set(listId, it.copy(items = it.items + item)) }
        }
    }

    override suspend fun getItems(listId: String) =
        lists.asFlow().mapNotNull { it[listId]?.items }

    override suspend fun deleteItem(listId: String, item: SlappItem) {
        lists.value = lists.value?.toMutableMap()?.apply {
            get(listId)?.let { set(listId, it.copy(items = it.items - item)) }
        }
    }

    override suspend fun getUsers(listId: String) =
        lists.asFlow().mapNotNull { it[listId]?.users }

    override suspend fun addUsers(listId: String, users: Iterable<Contact>) {
        lists.value = lists.value?.toMutableMap()?.apply {
            get(listId)?.let { set(listId, it.copy(users = it.users + users)) }
        }
    }

    private val lists = MutableLiveData(mapOf<String, SlappList>())
}