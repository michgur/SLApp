package com.klmn.slapp.data

import com.klmn.slapp.data.room.SlappDao
import com.klmn.slapp.data.room.entities.ItemEntityMapper
import com.klmn.slapp.data.room.entities.ListEntityMapper
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SlappRepository @Inject constructor(
    private val dao: SlappDao
) {
    fun getLists() = ListEntityMapper.toModelListFlow(dao.getLists())

    fun getList(id: Long) = ListEntityMapper.toModelFlow(dao.getList(id))

    fun getListName(id: Long) = dao.getListName(id)

    suspend fun addList(list: SlappList) = dao.addList(ListEntityMapper.toEntity(list).info)

    suspend fun addItem(listId: Long, item: SlappItem) =
        dao.addItem(ItemEntityMapper.toEntity(listId to item))

    fun getItems(listId: Long) = dao.getItems(listId).map { items ->
        ItemEntityMapper.toModelList(items).map { it.second }
    }

    suspend fun updateItem(listId: Long, item: SlappItem) = 
        dao.updateItem(ItemEntityMapper.toEntity(listId to item))

    suspend fun deleteItem(listId: Long, item: SlappItem) = 
        dao.deleteItem(ItemEntityMapper.toEntity(listId to item))
}
