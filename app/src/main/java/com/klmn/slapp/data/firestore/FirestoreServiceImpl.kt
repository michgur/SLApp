package com.klmn.slapp.data.firestore

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.klmn.slapp.data.firestore.entities.FirestoreEntities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
class FirestoreServiceImpl : FirestoreService {
    companion object {
        private const val COLLECTION = "lists"
        private const val TAG = "SLApp.firestore"
    }
    private val collection = Firebase.firestore.collection(COLLECTION)

    override suspend fun getLists(uid: String) = callbackFlow {
        val subscription = collection
            .whereArrayContains("users", uid)
            .addSnapshotListener { snapshot, e ->
                e?.let { Log.e(TAG, "${e.message}") }
                snapshot?.let {
                    offer(
                        snapshot.documents.mapNotNull {
                            it.toObject(FirestoreEntities.SList::class.java)
                        }
                    )
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun deleteList(list: FirestoreEntities.SList) {
        collection.document(list.id).delete().await()
    }

    override suspend fun addList(list: FirestoreEntities.SList) =
        collection.add(list).await().id

    override suspend fun setListName(listId: String, name: String) {
        collection.document(listId).update("name", name).await()
    }

    override suspend fun addUsers(listId: String, users: List<String>) {
        collection.document(listId)
            .update(
                "users", FieldValue.arrayUnion(*users.toTypedArray()),
                "tokens", FieldValue.arrayUnion(*Array(users.size) { "" })
            )
            .await()
    }

    override suspend fun addItem(listId: String, item: FirestoreEntities.Item) {
        collection.document(listId)
            .update("items", FieldValue.arrayUnion(item))
            .await()
    }

    override suspend fun deleteItem(listId: String, item: FirestoreEntities.Item) {
        collection.document(listId)
            .update("items", FieldValue.arrayRemove(item))
            .await()
    }
}