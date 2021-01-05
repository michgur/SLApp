package com.klmn.slapp.data.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface SlappDao {
    @Query("SELECT * FROM lists")
    fun getListEntities(): List<SlappDatabase.SList>
    fun getLists() = getListEntities().map(::toModelList)

    @Query("SELECT * FROM lists WHERE id = :id")
    fun getListEntity(id: Long): LiveData<SlappDatabase.SList>
    fun getList(id: Long): LiveData<SlappList> =
        Transformations.map(getListEntity(id), ::toModelList)

    @Query("SELECT name FROM lists WHERE id = :id")
    fun getListName(id: Long): String

    @Update
    fun updateListEntity(list: SlappDatabase.ListInfo)
    // does NOT update contents!
    fun updateList(list: SlappList) = updateListEntity(toListEntity(list).info)

    @Delete
    fun deleteListEntity(list: SlappDatabase.ListInfo)
    // does NOT delete contents!
    fun deleteList(list: SlappList) = deleteListEntity(toListEntity(list).info)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addListEntity(list: SlappDatabase.ListInfo): Long
    fun addList(list: SlappList): Long {
        val entity = toListEntity(list)
        val id = addListEntity(entity.info)
        entity.items.forEach(::addItemEntity)
        for (user in entity.users) addUser(id, user)
        return id
    }

    @Query("SELECT userId FROM users WHERE listId = :listId")
    fun getUsers(listId: Long): List<String>

    @Query("INSERT INTO users (listId, userId) VALUES (:listId, :user)")
    fun addUser(listId: Long, user: String)

    @Query("SELECT * FROM items WHERE listId = :listId")
    fun getItemEntities(listId: Long): Flow<List<SlappDatabase.Item>>
    fun getItems(listId: Long): Flow<List<SlappItem>> =
        getItemEntities(listId).map(::toModelItemList)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addItemEntity(item: SlappDatabase.Item)
    fun addItem(listId: Long, item: SlappItem) = addItemEntity(toItemEntity(listId, item))

    @Update
    fun updateItemEntity(item: SlappDatabase.Item)
    fun updateItem(listId: Long, item: SlappItem) = updateItemEntity(toItemEntity(listId, item))

    @Delete
    fun deleteItemEntity(item: SlappDatabase.Item)
    fun deleteItem(listId: Long, item: SlappItem) = deleteItemEntity(toItemEntity(listId, item))

    // use local functions instead of the @TypeConverter pattern,
    // since it can't convert to/from lists
    private fun toModelItem(item: SlappDatabase.Item) = SlappItem(
        item.name,
        item.user,
        item.timestamp
    )
    private fun toModelItemList(items: List<SlappDatabase.Item>) = items.map(::toModelItem)
    private fun toItemEntity(listId: Long, item: SlappItem) = SlappDatabase.Item(
        listId,
        item.name,
        item.user,
        item.timestamp
    )
    private fun toModelList(list: SlappDatabase.SList) = SlappList(
        list.info.id,
        list.info.name,
        list.info.user,
        list.info.timestamp,
        list.items.map(::toModelItem).toMutableList(),
        list.users
    )
    private fun toListEntity(list: SlappList) = SlappDatabase.SList(
        SlappDatabase.ListInfo(
            list.id,
            list.name,
            list.user,
            list.timestamp
        ),
        list.items.map {
            SlappDatabase.Item(
                list.id,
                it.name,
                it.user,
                it.timestamp
            )
        },
        list.users
    )
}
