package com.klmn.slapp

import android.app.Application
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@HiltAndroidApp
class SLApp : Application() {
    val mainThreadHandler = Handler(Looper.getMainLooper())

    val mainThread = Executor(mainThreadHandler::post)
    val background: Executor = Executors.newSingleThreadExecutor()
}