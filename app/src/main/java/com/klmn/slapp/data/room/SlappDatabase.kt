package com.klmn.slapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klmn.slapp.data.room.entities.RoomEntities

@Database(
    entities = [
        RoomEntities.ListInfo::class,
        RoomEntities.Item::class,
        RoomEntities.User::class
    ], version = 1
)
abstract class SlappDatabase : RoomDatabase() {
    abstract fun slappDao(): SlappDao
}