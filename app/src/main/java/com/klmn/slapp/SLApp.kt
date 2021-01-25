package com.klmn.slapp

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@HiltAndroidApp
class SLApp : Application() {
    val mainThreadHandler = Handler(Looper.getMainLooper())

    val mainThread = Executor(mainThreadHandler::post)
    val background: Executor = Executors.newSingleThreadExecutor()
}