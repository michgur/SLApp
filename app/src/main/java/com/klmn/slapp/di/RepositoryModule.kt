package com.klmn.slapp.di

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.SlappRepositoryImpl
import com.klmn.slapp.data.contacts.ContactProvider
import com.klmn.slapp.data.contacts.ContactsRepository
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.data.firestore.FirestoreService
import com.klmn.slapp.data.firestore.FirestoreServiceImpl
import com.klmn.slapp.data.firestore.entities.FirestoreEntities
import com.klmn.slapp.data.firestore.entities.FirestoreItemMapper
import com.klmn.slapp.data.firestore.entities.FirestoreListMapper
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideService() = FirestoreServiceImpl() as FirestoreService

    @Provides @Singleton
    fun provideListMapper(contactsRepository: ContactsRepository) =
        FirestoreListMapper(contactsRepository) as EntityModelMapper<FirestoreEntities.SList, SlappList>

    @Provides @Singleton
    fun provideItemMapper(contactsRepository: ContactsRepository) =
        FirestoreItemMapper(contactsRepository) as EntityModelMapper<FirestoreEntities.Item, SlappItem>

    @Provides @Singleton
    fun provideRepository(
        service: FirestoreService,
        listMapper: EntityModelMapper<FirestoreEntities.SList, SlappList>,
        itemMapper: EntityModelMapper<FirestoreEntities.Item, SlappItem>,
        contactProvider: ContactProvider,
        userPreferences: UserPreferences
    ) = SlappRepositoryImpl(service, listMapper, itemMapper, contactProvider, userPreferences) as SlappRepository
}