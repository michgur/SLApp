package com.klmn.slapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klmn.slapp.data.room.entities.Entities

@Database(
    entities = [
        Entities.ListInfo::class,
        Entities.Item::class,
        Entities.User::class
    ], version = 1
)
abstract class SlappDatabase : RoomDatabase() {
    abstract fun slappDao(): SlappDao
}