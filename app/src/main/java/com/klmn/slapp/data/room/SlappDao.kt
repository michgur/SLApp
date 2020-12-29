package com.klmn.slapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

@Dao
interface SlappDao {
    @Query("SELECT * FROM lists")
    fun getLists(): List<SlappList>

    @Query("SELECT * FROM lists WHERE id = :id")
    fun getList(id: Long): LiveData<SlappList>

    @Update
    fun updateList(list: SlappList)

    @Delete
    fun deleteList(list: SlappList)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addList(list: SlappList): Long

    @Query("SELECT userId FROM users WHERE listId = :listId")
    fun getUsers(listId: Long): List<String>

    @Query("SELECT * FROM items WHERE listId = :listId")
    fun getItems(listId: Long): List<SlappItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addItem(item: SlappDatabase.Item)

    fun addItem(listId: Long, item: SlappItem) = addItem(
        SlappDatabase.Item(listId, item.name, item.user, item.timestamp)
    )

    @Update
    fun updateItem(item: SlappItem)

    @Delete
    fun deleteItem(item: SlappItem)
}