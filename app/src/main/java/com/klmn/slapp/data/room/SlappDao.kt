package com.klmn.slapp.data.room

import androidx.room.*
import com.klmn.slapp.data.room.entities.Entities
import kotlinx.coroutines.flow.Flow

@Dao
interface SlappDao {
    @Query("SELECT * FROM lists WHERE id IN (SELECT listId FROM users WHERE userId = :uid) ORDER BY timestamp")
    fun getLists(uid: String): Flow<List<Entities.SList>>

    @Query("SELECT * FROM lists WHERE id = :id")
    fun getList(id: Long): Flow<Entities.SList>

    @Query("SELECT name FROM lists WHERE id = :id")
    fun getListName(id: Long): Flow<String>

    @Update
    suspend fun updateList(list: Entities.ListInfo)

    @Delete
    suspend fun deleteList(list: Entities.ListInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addList(list: Entities.ListInfo): Long

    @Query("SELECT userId FROM users WHERE listId = :listId")
    fun getUsers(listId: Long): Flow<List<String>>

    @Query("INSERT INTO users (listId, userId) VALUES (:listId, :user)")
    suspend fun addUser(listId: Long, user: String)

    @Query("SELECT * FROM items WHERE listId = :listId ORDER BY timestamp")
    fun getItems(listId: Long): Flow<List<Entities.Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItem(item: Entities.Item)

    @Update
    suspend fun updateItem(item: Entities.Item)

    @Delete
    suspend fun deleteItem(item: Entities.Item)
}
