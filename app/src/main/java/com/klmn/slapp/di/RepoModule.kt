package com.klmn.slapp.di

import android.content.Context
import androidx.room.Room
import com.klmn.slapp.SLApp
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.room.SlappDao
import com.klmn.slapp.data.room.SlappDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides @Singleton
    fun provideExecutor(@ApplicationContext context: Context): Executor =
        (context as SLApp).background

    @Provides
    fun provideRepository(executor: Executor, dao: SlappDao) = SlappRepository(executor, dao)

    @Provides
    fun provideDao(database: SlappDatabase) = database.slappDao()

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        SlappDatabase::class.java,
        "slapp_db"
    ).build()
}