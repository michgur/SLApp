package com.klmn.slapp.data.room

import androidx.room.*
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

@Dao
interface SlappDao {
    @Query("SELECT * FROM lists")
    fun getLists(): List<SlappList>

    @Query("SELECT * FROM lists WHERE id = :id")
    fun getList(id: Int): SlappList

    @Update
    fun updateList(list: SlappList)

    @Delete
    fun deleteList(list: SlappList)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addList(list: SlappList)

    @Query("SELECT userId FROM users WHERE listId = :listId")
    fun getUsers(listId: Int): List<String>

    @Query("SELECT * FROM items WHERE listId = :listId")
    fun getItems(listId: Int): List<SlappItem>

    @Update
    fun updateItem(item: SlappItem)

    @Delete
    fun deleteItem(item: SlappItem)
}