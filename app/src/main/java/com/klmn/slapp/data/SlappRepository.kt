package com.klmn.slapp.data

import com.klmn.slapp.common.task
import com.klmn.slapp.data.room.SlappDao
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import java.util.concurrent.Executor
import javax.inject.Inject

class SlappRepository @Inject constructor(
    private val executor: Executor,
    private val dao: SlappDao
) {
    fun getLists() = executor.task { dao.getLists() }
    fun getList(id: Long) = executor.task { dao.getList(id) }
    fun addList(list: SlappList) = executor.task { dao.addList(list) }
    fun addItem(listId: Long, item: SlappItem) = executor.task { dao.addItem(listId, item) }
    fun getItems(listId: Long) = executor.task { dao.getItems(listId) }
//    fun updateItem(item: SlappItem) = dao.updateItem(item)
//    fun deleteItem(item: SlappItem) = dao.deleteItem(item)
}
