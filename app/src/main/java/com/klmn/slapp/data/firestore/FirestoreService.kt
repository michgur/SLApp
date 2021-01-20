package com.klmn.slapp.data.firestore

import com.klmn.slapp.data.firestore.entities.FirestoreEntities
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    // list operations
    suspend fun getLists(uid: String): Flow<List<FirestoreEntities.SList>>

    suspend fun getList(id: String): Flow<FirestoreEntities.SList>

    suspend fun getListName(id: String): Flow<String>

    suspend fun updateList(list: FirestoreEntities.SList)

    suspend fun deleteList(list: FirestoreEntities.SList)

    suspend fun addList(list: FirestoreEntities.SList): String

    // user operations
    suspend fun getUsers(listId: String): Flow<List<String>>

    suspend fun addUser(listId: String, user: String)

    // item operations
    suspend fun getItems(listId: String): Flow<List<FirestoreEntities.Item>>

    suspend fun addItem(listId: String, item: FirestoreEntities.Item)

    suspend fun deleteItem(listId: String, item: FirestoreEntities.Item)

    // registration token operations
    suspend fun setTokens(listId: String, tokens: List<String>)
}