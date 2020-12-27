package com.klmn.slapp.domain

import com.google.android.gms.tasks.Task
import com.klmn.slapp.SlappItem
import com.klmn.slapp.SlappList

interface IListStorage {
    fun getList() : Task<SlappList>
    fun updateList() : Task<Unit>
    fun getItem(position: Int) : Task<SlappItem>
    fun updateItem(position: Int, item: SlappItem) : Task<Unit>
}