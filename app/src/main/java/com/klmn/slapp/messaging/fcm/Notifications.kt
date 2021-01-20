package com.klmn.slapp.messaging.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.klmn.slapp.R
import com.klmn.slapp.ui.MainActivity
import kotlin.random.Random

fun Service.pushNotification(title: String?, message: String?) {
    val intent = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationId = Random.nextInt()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(notificationManager)

    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    val notification = NotificationCompat.Builder(this, MessagingService.CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()
    notificationManager.notify(notificationId, notification)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createNotificationChannel(notificationManager: NotificationManager) {
    val channelName = "SLAppChannel"
    val channel = NotificationChannel(MessagingService.CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
        description = "SLApp Shopping Notifications"
        enableLights(true)
        lightColor = Color.WHITE
    }
    notificationManager.createNotificationChannel(channel)
}
