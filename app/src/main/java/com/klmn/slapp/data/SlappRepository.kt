package com.klmn.slapp.data

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.data.contacts.ContactProvider
import com.klmn.slapp.data.firestore.FirestoreService
import com.klmn.slapp.data.firestore.entities.FirestoreEntities
import com.klmn.slapp.data.firestore.entities.FirestoreItemMapper
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SlappRepository @Inject constructor(
    private val service: FirestoreService,
    private val listMapper: EntityModelMapper<FirestoreEntities.SList, SlappList>,
    private val itemMapper: EntityModelMapper<FirestoreEntities.Item, SlappItem>,
    private val contactProvider: ContactProvider
) {
    // todo: minimize document-reads by storing all of the lists as flows and mapping them
    //      here for list properties
    suspend fun addList(list: SlappList) = service.addList(listMapper.toEntity(list))

    suspend fun getLists(uid: String) = listMapper.toModelListFlow(service.getLists(uid))

    suspend fun getListName(id: String) = service.getListName(id)

    suspend fun addItem(listId: String, item: SlappItem) =
        service.addItem(listId, itemMapper.toEntity(item))

    suspend fun getItems(listId: String) = service.getItems(listId).map { items ->
        itemMapper.toModelList(items)
    }

    suspend fun getUsers(listId: String) = service.getUsers(listId).map { users ->
        users.map { contactProvider.getContact(it) ?: Contact(it) }
    }
    suspend fun addUsers(listId: String, users: Iterable<Contact>) {
        users.forEach { service.addUser(listId, it.phoneNumber) }
    }

    suspend fun updateItem(listId: String, item: SlappItem) = 
        service.updateItem(listId, itemMapper.toEntity(item))

    suspend fun deleteItem(listId: String, item: SlappItem) =
        service.deleteItem(listId, itemMapper.toEntity(item))
}
