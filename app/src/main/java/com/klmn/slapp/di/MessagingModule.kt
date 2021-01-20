package com.klmn.slapp.di

import com.klmn.slapp.messaging.fcm.NotificationAPI
import com.klmn.slapp.messaging.firestore.FirestoreTokenService
import com.klmn.slapp.messaging.firestore.FirestoreTokenServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MessagingModule {
    const val FCM_URL = "https://fcm.googleapis.com"

    @Provides @Singleton
    fun provideRetrofit() = Retrofit.Builder()
        .baseUrl(FCM_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton
    fun provideAPI(retrofit: Retrofit) = retrofit.create(NotificationAPI::class.java)

    @Provides @Singleton
    fun provideFirestoreTokenService() =
        FirestoreTokenServiceImpl() as FirestoreTokenService
}