package com.klmn.slapp.data

import com.klmn.slapp.common.EntityModelMapper
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
    private val itemMapper: EntityModelMapper<FirestoreEntities.Item, SlappItem>
) : SlappRepository {
    private var uid: String? = null
    private var _listsFlow = MutableStateFlow<Map<String, SlappList>?>(null)
    private var listsFlow = _listsFlow.filterNotNull()
    private val scope = CoroutineScope(Dispatchers.IO)

    private fun initLists(uid: String) = scope.launch {
        service.getLists(uid).map { lists ->
            lists.associate { it.id to listMapper.toModel(it) }
        }.let { _listsFlow.emitAll(it) }
    }

    override suspend fun addList(list: SlappList) = service.addList(listMapper.toEntity(list))

    override suspend fun getLists(uid: String): Flow<List<SlappList>> {
        if (this.uid != uid) {
            initLists(uid)
            this.uid = uid
        }
        return listsFlow.map { it.values.toList() }
    }

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
