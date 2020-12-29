package com.klmn.slapp.data.room

import androidx.room.*

@Database(entities = [SlappDatabase.ListInfo::class, SlappDatabase.Item::class, SlappDatabase.User::class], version = 1)
abstract class SlappDatabase : RoomDatabase() {
    abstract fun slappDao(): SlappDao

    @Entity(tableName = "lists")
    data class ListInfo(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        val user: String,
        val timestamp: Long
    )

    @Entity(tableName = "items", foreignKeys = [ForeignKey(
        entity = ListInfo::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onUpdate = ForeignKey.CASCADE)])
    data class Item(
        val listId: Long,
        val name: String,
        val user: String,
        @PrimaryKey val timestamp: Long
    )

    @Entity(tableName = "users", foreignKeys = [ForeignKey(
        entity = ListInfo::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onUpdate = ForeignKey.CASCADE)])
    data class User(
        val listId: Long,
        val userId: String,
        @PrimaryKey(autoGenerate = true) val index: Int
    )

    data class SList(
        @Embedded val info: ListInfo,
        @Relation(parentColumn = "id", entityColumn = "listId", entity = Item::class)
        val items: List<Item>,
        @Relation(parentColumn = "id", entityColumn = "listId", entity = User::class, projection = ["userId"])
        val users: List<String>
    )


}