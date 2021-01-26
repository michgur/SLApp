package com.klmn.slapp

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.User
import com.klmn.slapp.messaging.fcm.updateToken
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltAndroidApp
class SLApp : Application() {
    val mainThreadHandler = Handler(Looper.getMainLooper())

    val mainThread = Executor(mainThreadHandler::post)
    val background: Executor = Executors.newSingleThreadExecutor()

    @Inject lateinit var userPreferences: UserPreferences

    override fun onCreate() {
        super.onCreate()

        userPreferences.registrationToken.observeForever { token ->
            if (token != null) userPreferences.phoneNumber.value?.let { uid ->
                updateToken(User(uid, token))
            }
        }
    }
}