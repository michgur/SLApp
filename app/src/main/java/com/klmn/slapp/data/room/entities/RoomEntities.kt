package com.klmn.slapp.data.room.entities

import androidx.room.*

object RoomEntities {
    @Entity(tableName = "lists", indices = [Index(value = ["firestoreId"], unique = true)])
    data class ListInfo(
        @PrimaryKey(autoGenerate = true) val id: Long = 0, // offline ID
        val firestoreId: String,
        val name: String,
        val user: String,
        val timestamp: Long,
        val state: Int
    )

    @Entity(
        tableName = "items", foreignKeys = [ForeignKey(
            entity = ListInfo::class,
            parentColumns = ["firestoreId"],
            childColumns = ["listId"],
            onUpdate = ForeignKey.CASCADE
        )]
    )
    data class Item(
        val listId: String,
        val name: String,
        val user: String,
        @PrimaryKey val timestamp: Long,
        val state: Int
    )

    @Entity(
        tableName = "users", foreignKeys = [ForeignKey(
            entity = ListInfo::class,
            parentColumns = ["firestoreId"],
            childColumns = ["listId"],
            onUpdate = ForeignKey.CASCADE
        )]
    )
    data class User(
        val listId: String,
        val userId: String,
        @PrimaryKey(autoGenerate = true) val index: Int,
        //val state: Int todo
    )

    data class SList(
        @Embedded val info: ListInfo,
        @Relation(parentColumn = "firestoreId", entityColumn = "listId", entity = Item::class)
        val items: List<Item>,
        @Relation(
            parentColumn = "firestoreId",
            entityColumn = "listId",
            entity = User::class,
            projection = ["userId"]
        )
        val users: List<String>
    )
}