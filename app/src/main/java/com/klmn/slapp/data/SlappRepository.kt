package com.klmn.slapp.data

import com.klmn.slapp.data.firestore.FirestoreService
import com.klmn.slapp.data.firestore.entities.ItemFirestoreMapper
import com.klmn.slapp.data.firestore.entities.ListFirestoreMapper
import com.klmn.slapp.data.room.SlappDao
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SlappRepository @Inject constructor(
    private val service: FirestoreService,
    private val dao: SlappDao
) {
    suspend fun getLists(uid: String) = ListFirestoreMapper.toModelListFlow(service.getLists(uid))

    suspend fun getList(id: String) = ListFirestoreMapper.toModelFlow(service.getList(id))

    suspend fun getListName(id: String) = service.getListName(id)

    suspend fun addList(list: SlappList) = service.addList(ListFirestoreMapper.toEntity(list))

    suspend fun addItem(listId: String, item: SlappItem) =
        service.addItem(listId, ItemFirestoreMapper.toEntity(item))

    suspend fun getItems(listId: String) = service.getItems(listId).map { items ->
        ItemFirestoreMapper.toModelList(items)
    }

    suspend fun getUsers(listId: String) = service.getUsers(listId)
    suspend fun addUsers(listId: String, users: List<String>) {
        users.forEach { service.addUser(listId, it) }
    }

    suspend fun updateItem(listId: String, item: SlappItem) = 
        service.updateItem(listId, ItemFirestoreMapper.toEntity(item))

    suspend fun deleteItem(listId: String, item: SlappItem) =
        service.deleteItem(listId, ItemFirestoreMapper.toEntity(item))
}
