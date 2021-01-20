package com.klmn.slapp.di

import android.content.Context
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.messaging.fcm.NotificationAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
}