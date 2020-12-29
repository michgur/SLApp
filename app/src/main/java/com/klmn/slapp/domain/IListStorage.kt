package com.klmn.slapp.domain

import com.google.android.gms.tasks.Task
import com.klmn.slapp.SlappItem
import com.klmn.slapp.SlappList

interface IListStorage {
    fun getList() : Task<SlappList>
    fun updateList(list: SlappList) : Task<Unit>
    fun deleteList() : Task<SlappList>
    fun getItem(position: Int) : Task<SlappItem>
    fun addItem(item: SlappItem) : Task<Unit>
    fun updateItem(position: Int, item: SlappItem) : Task<Unit>
    fun removeItem(position: Int) : Task<SlappItem>
}