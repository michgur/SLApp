package com.klmn.slapp.data

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.data.firestore.FirestoreService
import com.klmn.slapp.data.firestore.entities.FirestoreEntities
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SlappRepositoryImpl(
    private val service: FirestoreService,
    private val listMapper: EntityModelMapper<FirestoreEntities.SList, SlappList>,
    private val itemMapper: EntityModelMapper<FirestoreEntities.Item, SlappItem>,
    userPreferences: UserPreferences
) : SlappRepository {
    private var listsFlow = MutableStateFlow(mapOf<String, SlappList>())
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        userPreferences.phoneNumber.observeForever { uid ->
            scope.launch {
                service.getLists(uid).map { lists ->
                    lists.associate { it.id to listMapper.toModel(it) }
                }.let { listsFlow.emitAll(it) }
            }
        }
    }

    // todo: check if a snapshot listener of all lists will actually read all of them everytime
    //          instead of just the changed one
    override suspend fun addList(list: SlappList) = service.addList(listMapper.toEntity(list))

    override suspend fun getLists(uid: String) = listsFlow.map { it.values.toList() }

    override suspend fun getListName(listId: String) =
        listsFlow.mapNotNull { it[listId]?.name }.distinctUntilChanged()

    override suspend fun setListName(listId: String, name: String) =
        service.setListName(listId, name)

    override suspend fun addItem(listId: String, item: SlappItem) =
        service.addItem(listId, itemMapper.toEntity(item))

    override suspend fun getItems(listId: String) =
        listsFlow.mapNotNull { it[listId]?.items }.distinctUntilChanged()

    override suspend fun deleteItem(listId: String, item: SlappItem) =
        service.deleteItem(listId, itemMapper.toEntity(item))

    override suspend fun getUsers(listId: String) =
        listsFlow.mapNotNull { it[listId]?.users }.distinctUntilChanged()

    override suspend fun addUsers(listId: String, users: Iterable<Contact>) =
        service.addUsers(listId, users.map { it.phoneNumber })
}
