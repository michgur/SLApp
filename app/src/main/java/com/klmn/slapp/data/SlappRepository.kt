package com.klmn.slapp.data

import com.klmn.slapp.Task
import com.klmn.slapp.data.room.SlappDao
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import java.util.concurrent.Executor
import javax.inject.Inject

class SlappRepository @Inject constructor(
    private val executor: Executor,
    private val dao: SlappDao
) {
    fun getLists() = Task(executor) { dao.getLists() }
    fun getList(id: Long) = Task(executor) { dao.getList(id) }
    fun addList(list: SlappList) = Task(executor) { dao.addList(list) }
    fun addItem(listId: Long, item: SlappItem) = Task(executor) { dao.addItem(listId, item) }
//    fun updateItem(item: SlappItem) = dao.updateItem(item)
//    fun deleteItem(item: SlappItem) = dao.deleteItem(item)
}
