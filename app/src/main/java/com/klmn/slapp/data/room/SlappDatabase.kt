package com.klmn.slapp.data.room

import androidx.room.*
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

@Database(entities = [SlappDatabase.ListInfo::class, SlappDatabase.Item::class, SlappDatabase.User::class], version = 1)
@TypeConverters(SlappDatabase.Converters::class)
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
        parentColumns = ["name"],
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
        parentColumns = ["name"],
        childColumns = ["listId"],
        onUpdate = ForeignKey.CASCADE)])
    data class User(
        val listId: Long,
        val userId: String
    )

    data class SList(
        @Embedded val info: ListInfo,
        @Relation(parentColumn = "name", entityColumn = "listId", entity = Item::class)
        val items: List<Item>,
        @Relation(parentColumn = "name", entityColumn = "listId", entity = User::class)
        val users: List<User>
    )

    object Converters {
        @TypeConverter
        fun toModelItem(item: Item) = SlappItem(
            item.name,
            item.user,
            item.timestamp
        )

        @TypeConverter
        fun toModelList(list: SList) = SlappList(
            list.info.id,
            list.info.name,
            list.info.user,
            list.info.timestamp,
            list.items.map(::toModelItem).toMutableList(),
            list.users.map { it.userId }
        )

        @TypeConverter
        fun toListEntity(list: SlappList) = SList(
            ListInfo(
                list.id,
                list.name,
                list.user,
                list.timestamp
            ),
            list.items.map {
                Item(
                    0,
                    it.name,
                    it.user,
                    it.timestamp
                )
            },
            list.users.map {
                User(
                    0,
                    it
                )
            }
        )
    }
}