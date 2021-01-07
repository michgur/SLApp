package com.klmn.slapp.di

import android.content.Context
import com.klmn.slapp.data.contacts.ContactProvider
import com.klmn.slapp.data.contacts.ContactsRepository
import com.klmn.slapp.data.contacts.ContactsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContactsModule {
    @Provides @Singleton
    fun provideContactProvider(@ApplicationContext context: Context) =
        ContactProvider(context.contentResolver)

    @Provides @Singleton
    fun provideContactsRepository(contactProvider: ContactProvider) =
        ContactsRepositoryImpl(contactProvider) as ContactsRepository
}