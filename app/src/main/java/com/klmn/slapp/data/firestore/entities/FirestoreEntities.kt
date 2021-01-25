package com.klmn.slapp.data.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

object FirestoreEntities {
    data class Item(
        val name: String = "",
        val user_id: String = "",
        val timestamp: Timestamp = Timestamp.now()
    )

    data class SList(
        @DocumentId val id: String = "",
        val name: String = "",
        val created_by: String = "",
        val timestamp: Timestamp = Timestamp.now(),
        val users: List<String> = listOf(),
        val items: List<Item> = listOf(),
        @Exclude var isNew: Boolean = false
    )
}