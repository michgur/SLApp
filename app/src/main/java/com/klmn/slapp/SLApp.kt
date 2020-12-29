package com.klmn.slapp

import android.app.Application
import androidx.room.Room
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.room.SlappDatabase

class SLApp : Application() {
    lateinit var repository: SlappRepository private set

    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(
            this,
            SlappDatabase::class.java,
            "slapp"
        ).build()
        repository = SlappRepository(db.slappDao())
    }
}