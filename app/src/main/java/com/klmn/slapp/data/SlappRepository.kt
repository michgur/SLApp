package com.klmn.slapp.data

import com.klmn.slapp.data.room.SlappDao
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

class SlappRepository(private val dao: SlappDao) {
    fun getLists() = dao.getLists()
    fun getList(id: Long) = dao.getList(id)
    fun addList(list: SlappList) = dao.addList(list)
    fun addItem(listId: Long, item: SlappItem) = dao.addItem(listId, item)
//    fun updateItem(item: SlappItem) = dao.updateItem(item)
//    fun deleteItem(item: SlappItem) = dao.deleteItem(item)
}