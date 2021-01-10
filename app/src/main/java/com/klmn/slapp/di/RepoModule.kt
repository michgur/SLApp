package com.klmn.slapp.di

import android.content.Context
import androidx.room.Room
import com.klmn.slapp.SLApp
import com.klmn.slapp.common.DATABASE_NAME
import com.klmn.slapp.data.firestore.FirestoreService
import com.klmn.slapp.data.firestore.FirestoreServiceImpl
import com.klmn.slapp.data.room.SlappDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.Executor
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides @Singleton
    fun provideExecutor(@ApplicationContext context: Context): Executor =
        (context as SLApp).background

    @Provides @Singleton
    fun provideDao(database: SlappDatabase) = database.slappDao()

    @Provides @Singleton
    fun provideService() = FirestoreServiceImpl() as FirestoreService

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        SlappDatabase::class.java,
        DATABASE_NAME
    ).build()
}