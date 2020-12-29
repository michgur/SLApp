package com.klmn.slapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@HiltAndroidApp
class SLApp : Application() {
    val background: Executor = Executors.newSingleThreadExecutor()
}