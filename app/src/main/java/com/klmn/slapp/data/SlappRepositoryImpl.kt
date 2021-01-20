package com.klmn.slapp.data

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.data.contacts.ContactProvider
import com.klmn.slapp.data.firestore.FirestoreService
import com.klmn.slapp.data.firestore.entities.FirestoreEntities
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@ExperimentalCoroutinesApi
class SlappRepositoryImpl constructor(
    private val service: FirestoreService,
    private val listMapper: EntityModelMapper<FirestoreEntities.SList, SlappList>,
    private val itemMapper: EntityModelMapper<FirestoreEntities.Item, SlappItem>,
    private val contactProvider: ContactProvider
) : SlappRepository {
    // todo: minimize document-reads by storing all of the lists as flows and mapping them
    //      here for list properties
    override suspend fun addList(list: SlappList) = service.addList(listMapper.toEntity(list))

    override suspend fun getLists(uid: String) = listMapper.toModelListFlow(service.getLists(uid))

    override suspend fun getListName(id: String) = service.getListName(id)

    override suspend fun addItem(listId: String, item: SlappItem) =
        service.addItem(listId, itemMapper.toEntity(item))

    override suspend fun getItems(listId: String) = service.getItems(listId).map { items ->
        itemMapper.toModelList(items)
    }

    override suspend fun deleteItem(listId: String, item: SlappItem) =
        service.deleteItem(listId, itemMapper.toEntity(item))

    // this won't work for tokens
    override suspend fun getUsers(listId: String) = service.getUsers(listId).map { users ->
        users.map { contactProvider.getContact(it) ?: Contact(it) }
    }

    override suspend fun addUsers(listId: String, users: Iterable<Contact>) {
        users.forEach { service.addUser(listId, it.phoneNumber) }
    }

    override suspend fun addToken(uid: String, token: String, listId: String) {
        getUsers(listId).stateIn(CoroutineScope(Dispatchers.IO)).value.map {
            if (it.phoneNumber == uid) token
            else it.registrationToken ?: ""
        }.let { service.setTokens(listId, it) }
    }

    override suspend fun refreshToken(uid: String, token: String) {
        getLists(uid).stateIn(CoroutineScope(Dispatchers.IO)).value.forEach { list ->
            list.users.map {
                if (it.phoneNumber == uid) token
                else it.registrationToken ?: ""
            }.let { service.setTokens(list.id, it) }
        }
    }
}
