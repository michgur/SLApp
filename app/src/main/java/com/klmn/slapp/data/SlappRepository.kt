package com.klmn.slapp.data

import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.flow.Flow

interface SlappRepository {
    suspend fun addList(list: SlappList): String

    suspend fun getLists(uid: String): Flow<List<SlappList>>

    suspend fun getListName(id: String): Flow<String>

    suspend fun addItem(listId: String, item: SlappItem)

    suspend fun getItems(listId: String): Flow<List<SlappItem>>

    suspend fun getUsers(listId: String): Flow<List<Contact>>

    suspend fun addUsers(listId: String, users: Iterable<Contact>)

    suspend fun updateItem(listId: String, item: SlappItem)

    suspend fun deleteItem(listId: String, item: SlappItem)
}
