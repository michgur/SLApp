package com.klmn.slapp.data.firestore

import android.util.Log
import com.google.firebase.Timestamp
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
        private const val TAG = "data.firestore"
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

    override suspend fun getList(id: String) = callbackFlow {
        val subscription = collection.document(id)
            .addSnapshotListener { snapshot, e ->
                e?.let { Log.e(TAG, "${e.message}") }
                snapshot?.let {
                    offer(snapshot.toObject(FirestoreEntities.SList::class.java)!!)
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getListName(id: String) = callbackFlow {
        val subscription = collection.document(id)
            .addSnapshotListener { snapshot, e ->
                e?.let { Log.e(TAG, "${e.message}") }
                snapshot?.let {
                    offer(snapshot.getString("name")!!)
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun updateList(list: FirestoreEntities.SList) {
        collection.document(list.id).set(list).await()
    }

    override suspend fun deleteList(list: FirestoreEntities.SList) {
        collection.document(list.id).delete().await()
    }

    override suspend fun addList(list: FirestoreEntities.SList) =
        collection.add(list).await().id

    override suspend fun getUsers(listId: String) = callbackFlow {
        val subscription = collection.document(listId)
            .addSnapshotListener { snapshot, e ->
                e?.let { Log.e(TAG, "${e.message}") }
                snapshot?.let {
                    offer((snapshot.get("users") as List<*>).filterNotNull().map(Any::toString))
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun addUser(listId: String, user: String) {
        collection.document(listId)
            .update("users", FieldValue.arrayUnion(user))
            .await()
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getItems(listId: String) = callbackFlow {
        val subscription = collection.document(listId)
            .addSnapshotListener { snapshot, e ->
                e?.let { Log.e(TAG, "${e.message}") }
                snapshot?.let {
                    offer(
                        (snapshot.get("items") as List<HashMap<String, *>>).map {
                            FirestoreEntities.Item(
                                it["name"] as String,
                                it["user_id"] as String,
                                it["timestamp"] as Timestamp
                            )
                        }
                    )
                }
            }

        awaitClose { subscription.remove() }
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