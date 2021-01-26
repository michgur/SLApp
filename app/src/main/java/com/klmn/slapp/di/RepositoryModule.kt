package com.klmn.slapp.di

import android.content.Context
import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.SlappRepositoryImpl
import com.klmn.slapp.data.contacts.ContactProvider
import com.klmn.slapp.data.contacts.ContactProviderImpl
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideContactProvider(
        @ApplicationContext context: Context,
        userPreferences: UserPreferences
    ) = ContactProviderImpl(context, userPreferences) as ContactProvider

    @Provides @Singleton
    fun provideService() = FirestoreServiceImpl() as FirestoreService

    @Provides @Singleton
    fun provideListMapper(contactProvider: ContactProvider) =
        FirestoreListMapper(contactProvider) as EntityModelMapper<FirestoreEntities.SList, SlappList>

    @Provides @Singleton
    fun provideItemMapper(contactProvider: ContactProvider) =
        FirestoreItemMapper(contactProvider) as EntityModelMapper<FirestoreEntities.Item, SlappItem>

    @Provides @Singleton
    fun provideRepository(
        service: FirestoreService,
        listMapper: EntityModelMapper<FirestoreEntities.SList, SlappList>,
        itemMapper: EntityModelMapper<FirestoreEntities.Item, SlappItem>
    ) = SlappRepositoryImpl(service, listMapper, itemMapper) as SlappRepository
}