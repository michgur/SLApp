package com.klmn.slapp

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.User
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
            if (token == null) return@observeForever
            userPreferences.phoneNumber.value?.let { uid ->
                Firebase.functions("europe-west1")
                    .getHttpsCallable("updateToken")
                    .call(Gson().toJson(User(uid, token)))
                    .addOnFailureListener { it.printStackTrace() }
            }
        }
    }
}